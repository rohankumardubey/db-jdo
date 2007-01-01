/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
 
package org.apache.jdo.tck.api.jdohelper;

import javax.jdo.JDOHelper;
import javax.jdo.Transaction;

import org.apache.jdo.tck.JDO_Test;
import org.apache.jdo.tck.pc.mylib.PCPoint;
import org.apache.jdo.tck.util.BatchTestRunner;

/**
 *<B>Title:</B> Is Transactional
 *<BR>
 *<B>Keywords:</B> jdohelper 
 *<BR>
 *<B>Assertion ID:</B> A8.4.2-1
 *<BR>
 *<B>Assertion Description: </B>
  The jdohelper.isTransactional method returns true and delegates to the parameter instance 
  and instances whose state is associated with the current transaction .  evaluating to true when == is used. 
 */
 /*
 * Revision History
 * ================
 * Author         :   	Date   : 	Version  
 * Azita Kamangar  	10/4/01		 1.0
 */
public class IsTransactional extends JDO_Test {

    /** */
    private static final String ASSERTION_FAILED = 
        "Assertion A8.4.2-1 (IsTransactional) failed: ";
    
    /**
     * The <code>main</code> is called when the class
     * is directly executed from the command line.
     * @param args The arguments passed to the program.
     */
    public static void main(String[] args) {
        BatchTestRunner.run(IsTransactional.class);
    }
 
    /* test JDOHelper.IsTransactional(Object pc)
     *  
     */
    public void testIsTransactional() {
    	pm = getPM();
    	Transaction tx = pm.currentTransaction();
    	tx.begin();
    	PCPoint p1 = new PCPoint(1,3);
    	pm.makePersistent(p1);
        boolean transactional = JDOHelper.isTransactional(p1);
        tx.commit();
        if (!transactional)
            fail(ASSERTION_FAILED,
                 "JDOHelper.isTransactional returns false for P-NEW instance");

        tx.begin();
        // check transactional flag in new tx  
        transactional = JDOHelper.isTransactional(p1);
        tx.commit();
        if (transactional)
            fail(ASSERTION_FAILED,
                 "JDOHelper.isTransactional returns true for P-HOLLOW/P-NONTX instance");
        pm.close();
        pm = null;
    }
}
