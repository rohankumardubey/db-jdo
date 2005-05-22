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

/**
* Represents a request to cause operations since the previous Commit or
* Rollback request to rollback.
*
* @author Dave Bristor
*/
//
// This is client-side code.  It does not need to live in the server.
//
class RollbackRequest extends AbstractRequest {
    RollbackRequest(Message m, FOStorePMF pmf) {
        super(m, pmf);
    }

    //
    // Methods from AbstractRequest
    //

    /**
     * Provides the information ecessary for a RollbackRequest.
     * The format of this request is (aside from the request header):
     * <pre>
     * empty: that's right, there's nothing here; the request's existence
     * is alone enough to cause a rollback.
     * </pre>
     */
    protected void doRequestBody() throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("RollbackRequest.dRB"); // NOI18N
        }
    }

    //
    // Methods from Request
    //

    /**
     * This should never be executed.  A RollbackRequest causes the reply
     * data's header's Status value to indicate that the user requested a
     * rollback, and when the reply handler sees that value, it does not
     * invoke handleReply on any replies.
     */
    public void handleReply(Status status, DataInput in, int length)
        throws IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("RollbackRequest.hR"); // NOI18N
        }
    }
}
