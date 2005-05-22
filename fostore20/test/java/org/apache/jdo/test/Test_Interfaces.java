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

package org.apache.jdo.test;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.apache.jdo.pc.PCInterfaces;
import org.apache.jdo.test.util.JDORITestRunner;

/**
* Tests that arrays can be stored in the datastore.
*
* @author Dave Bristor
*/
public class Test_Interfaces extends Test_Fetch {

    /** */
    public static void main(String args[]) {
        JDORITestRunner.run(Test_Interfaces.class);
    }

    /** */
    public void test() throws Exception {
        insertObjects();
        readObjects();
        checkExtent(PCInterfaces.class, 1);
    }

    // We override this from Test_Fetch and insert our own objects
    protected void insertObjects() {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            PCInterfaces pcInterfaces = new PCInterfaces();
            pcInterfaces.init();
            if (debug) logger.debug("Before insert: " + pcInterfaces);
            pm.makePersistent(pcInterfaces);
            tx.commit();
        
            Object oid1 = JDOHelper.getObjectId(pcInterfaces);
            if (debug) logger.debug("inserted pcInterfaces: " + oid1);
            oids.add(oid1);
        }
        finally {
            if (tx != null && tx.isActive())
                tx.rollback();
            if (pm != null && !pm.isClosed())
                pm.close();
        }
    }

    /**
     * redefine verify called by readObjects to check whether the read
     * instance is correct.
     */
    protected void verify(int i, Object pc) {
        if (i > 0)
            fail("Wrong number of inserted objects, expected only one");
        PCInterfaces expected = new PCInterfaces();
        expected.init();
        assertEquals("Wrong instance returned from datastore", expected, pc);
    }
}
