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
 
package org.apache.jdo.tck.models.fieldtypes;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.apache.jdo.tck.JDO_Test;
import org.apache.jdo.tck.pc.fieldtypes.FieldsOfPrimitivedouble;
import org.apache.jdo.tck.util.BatchTestRunner;

/**
 *<B>Title:</B> Support of field type double.
 *<BR>
 *<B>Keywords:</B> model
 *<BR>
 *<B>Assertion ID:</B> A6.4.3-8.
 *<BR>
 *<B>Assertion Description: </B>
JDO implementations must support fields of the primitive type <code>double</code>.
 */


public class TestFieldsOfPrimitivedouble extends JDO_Test {
    
   /** */
    private static final String ASSERTION_FAILED = 
        "Assertion A6.4.3-8 (TestFieldsOfPrimitivedouble) failed: ";
    
    /**
     * The <code>main</code> is called when the class
     * is directly executed from the command line.
     * @param args The arguments passed to the program.
     */
    public static void main(String[] args) {
        BatchTestRunner.run(TestFieldsOfPrimitivedouble.class);
    }   

    /**
     * @see JDO_Test#localSetUp()
     */
    protected void localSetUp() {
        addTearDownClass(FieldsOfPrimitivedouble.class);
    }
    
    /** */
    public void test() {
        pm = getPM();

        runTest(pm);

        pm.close();
        pm = null;
    }

    /** */
    void runTest(PersistenceManager pm)
    {
        Transaction tx = pm.currentTransaction();
        int i, n;
        double value;
        tx.begin();
        FieldsOfPrimitivedouble pi = new FieldsOfPrimitivedouble();
        pi.identifier = 1;
        pm.makePersistent(pi);
        Object oid = pm.getObjectId(pi);
        n = pi.getLength();
        // Provide initial set of values
        for( i = 0, value = (double)10.15; i < n; ++i){
            pi.set( i, value);
        }
        tx.commit();
        // cache will be flushed
        pi = null;
        System.gc();

        tx.begin();

        pi = (FieldsOfPrimitivedouble) pm.getObjectById(oid, true);
        checkValues(oid, (double)10.15);

        // Provide new set of values
        for( i = 0, value = (double)68000.15; i < n; ++i){
            pi.set(i, value);
        }
        tx.commit();
        // cache will be flushed
        pi = null;
        System.gc();

        tx.begin();
        // check new values
        checkValues(oid, (double)68000.15);
        tx.commit();
    }

    /** */
    private void checkValues(Object oid, double startValue){
        int i;
        FieldsOfPrimitivedouble pi = (FieldsOfPrimitivedouble)
                pm.getObjectById(oid, true);
        int n = pi.getLength();
        for( i = 0; i < n; ++i){
            if( !FieldsOfPrimitivedouble.isPersistent[i] ) continue;
            double val = pi.get(i);
            if( val != startValue ){
                fail(ASSERTION_FAILED,
                        "Incorrect value for " +
                        FieldsOfPrimitivedouble.fieldSpecs[i] +
                        ", expected value " + startValue +
                        ", value is " + val);
            }
        }
    }
}