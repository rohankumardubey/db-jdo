/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package org.apache.jdo.impl.fostore;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;

import javax.jdo.JDODataStoreException;
import javax.jdo.JDOFatalDataStoreException;
import javax.jdo.JDOFatalUserException;
import javax.jdo.JDOOptimisticVerificationException;
import javax.jdo.JDOUserException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jdo.state.StateManagerInternal;
import org.apache.jdo.store.Connector;
import org.apache.jdo.util.I18NHelper;

/**
* Processes replies that are received from the store.  Dispatches each one to
* its corresponding Request.
* @author Dave Bristor
*/

// Perhaps this class isn't necessary, and it's functionality should be
// moved into AbstractRequest.  In any case, either it should be called
// ReplyProcessor or the single method should be named handleReplies.

// This is client-side code.  It does not need to live in the server.
abstract class ReplyHandler {
    /** I18N support. */
    private static final I18NHelper msg = I18NHelper.getInstance(I18N.NAME);

    /** Logger */
    static final Log logger = LogFactory.getFactory().getInstance(
        "org.apache.jdo.impl.fostore"); // NOI18N
    
    /**
    * Process all replies in the given input stream.  The format of the
    * DataInput is<br>
    * <pre>
    * Version number of the Reply data (of the whole enchilada, not of the
    * individual Reply instances).
    * Status value indicating the overall success by the server in processing
    * the Message.
    * </pre>
    * The expected Status value is either OK or FATAL.  If FATAL, then the
    * next item is
    * <pre>
    * String: message from server (such as exception string or stack trace).
    * </pre>
    * Otherwise, the next item is
    * <pre>
    * int: number of replies
    * </pre>
    * In the FATAL case, all other data is ignored.  Otherwise the remaining
    * data is, per reply:
    * <pre>
    * RequestId: of the request corresponding to the reply data being read
    * Status: of the individual reply
    * MessagePos: int which indicates where in the DataInput is a String that
    * was generated by the processing of the reply's request.  If this is 0,
    * then there is no message.
    * length: int indicating the length of the reply's data.
    * request-specific data: length bytes of data associated with the reply.
    * </pre>
    */
    static void processReplies(DataInput in, Message message) {
        ArrayList exceptions = new ArrayList();
        boolean optimistic_failure = false;

        try {
            try {
                Reply.verifyVersionNumber(in);
            } catch (JDOFatalUserException ex) {
                throw ex;
            }

            // Overall status of a reply is FATAL, ROLLBACK, LOGIN, or OK.
            Status replyStatus = new Status(in);
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "ReplyHandler.hR: replyStatus=" + replyStatus); // NOI18N
            }
            if (replyStatus.equals(Status.FATAL)) {
                // Don't process any replies, just throw a
                // JDOFatalDataStoreException so that upper levels can
                // rollback, etc. as necessary.
                Connector c = message.getConnector();
                if (null != c) {
                    c.setRollbackOnly();
                }
                String str = in.readUTF();
                // sic: DataStoreException.  This was decided upon after much
                // email discussion.
                throw new JDODataStoreException(
                    msg.msg("ERR_FatalReply", str)); // NOI18N

            } else if (replyStatus.equals(Status.LOGIN)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("ReplyHandler: Login failure"); // NOI18N
                }
                String str = in.readUTF();
                throw new JDOFatalDataStoreException(str);

            } else if (replyStatus.equals(Status.ROLLBACK)) {
                // The rollback was at user request.  Don't process any
                // replies, but don't throw any exception either.
                if (logger.isDebugEnabled()) {
                    logger.debug(
                        "ReplyHandler.processReplies: ROLLBACK"); // NOI18N
                }
                int skipLength = in.readInt();
                in.skipBytes(skipLength);
            } else {
                // Process each reply.
                int numReplies = in.readInt();
                if (logger.isDebugEnabled()) {
                    logger.debug(
                        "ReplyHandler.processReplies: numReplies=" + // NOI18N
                        numReplies);
                }

                for (int i = 0; i < numReplies; i++) {
                    RequestId requestId = new RequestId(in);
                    Status status = new Status(in);

                    if (logger.isDebugEnabled()) {
                        logger.debug(
                            "ReplyHandler: " + requestId + // NOI18N
                            ", " + status); // NOI18N
                    }

                    int messagePos = in.readInt();
                    Request request = message.getRequest(requestId);
                    if (null == request) {
                        // This could happen if a "nested" request is being
                        // processed, i.e. this requestId was already removed
                        // from the list but was not finished processing.
                        int length = in.readInt();
                        in.skipBytes(length);
                        continue;
                        /*
                        throw new FOStoreFatalInternalException(
                            ReplyHandler.class, "processReplies", // NOI18N
                            msg.msg(
                                "ERR_CannotGetRequest", requestId)); // NOI18N
                        */
                    }
                    int length = in.readInt();

                    if (0 != messagePos) {
                        if (status.equals(Status.OK) ||
                            status.equals(Status.WARN)) {

                            // If there's a message with non-erroneus status,
                            // let request handle reply.  Note: Request
                            // *MUST* read message in this case!
                            request.handleReply(status, in, length);


                            // The next 2 cases are similar, differing only
                            // in whether the user could retry the operation
                            // or not.  In both cases, skip the reply data
                            // (it might not even be valid, though it's
                            // length is) and add the message in an
                            // exception.
                        } else if (status.equals(Status.FATAL)) {
                            in.skipBytes(length);
                            String str = in.readUTF();
                            throw new JDOFatalDataStoreException(str);
                        } else if (status.equals(Status.OPTIMISTIC)) {
                            // overall status is failed due to optimistic verification
                            optimistic_failure = true;
                            // when the result is Status.OPTIMISTIC, the result
                            // contains the oid of the failed operation
                            OID oid = OID.read(in);
                            StateManagerInternal sm = request.getStateManager();
                            Object failed = (sm == null) ? null : sm.getObject();
                            String str = in.readUTF();
                            exceptions.add(new JDOOptimisticVerificationException(str, failed));
                        } else {
                            in.skipBytes(length);
                            String str = in.readUTF();
                            exceptions.add(new JDODataStoreException(str));
                        }
                    } else if (status.equals(Status.ERROR) ||
                               status.equals(Status.FATAL)) {
                        // If there's no message but the status reports an
                        // error then we have a bug.
                        throw new JDOFatalDataStoreException();
                    } else {
                        // No message, non-ERROR status: let request handle
                        // reply
                        request.handleReply(status, in, length);
                    }
                }
            }
        } catch (IOException ex) {
            throw new FOStoreFatalIOException(
                ReplyHandler.class, "processReplies", ex); // NOI18N
        }

        int numExceptions = exceptions.size();

        if (logger.isDebugEnabled()) {
            logger.debug(
                "ReplyHandler.processReplies: finished; " + // NOI18N
                "numExceptions=" + numExceptions); // NOI18N
        }

        if (numExceptions > 0) {
            // We can't use exceptions.toArray() because you wouldn't have
            // the debugging output.
            // XXX use toArray(Throwable[])
            Throwable t[] = new Throwable[numExceptions];
            for (int i = 0; i < numExceptions; i++) {
                t[i] = (Throwable)exceptions.get(i);
                if (logger.isDebugEnabled()) {
                    logger.debug("RH.rH: " + t[i]); // NOI18N
                }
            }
            if (optimistic_failure) {
                throw new JDOOptimisticVerificationException(
                    msg.msg("EXC_Optimistic"), t); // NOI18N
            } else {
                throw new JDODataStoreException(
                    msg.msg("EXC_Exceptions"), t);  // NOI18N
            }
        }
    }
}