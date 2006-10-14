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

package org.apache.jdo.tck.query;

import java.util.Collection;
import java.util.HashSet;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.jdo.tck.pc.company.Employee;
import org.apache.jdo.tck.pc.company.CompanyModelReader;
import org.apache.jdo.tck.util.BatchTestRunner;

/**
 *<B>Title:</B> StartsWith and EndsWith Query Operators
 *<BR>
 *<B>Keywords:</B> query
 *<BR>
 *<B>Assertion ID:</B> A14.6.2-33.
 *<BR>
 *<B>Assertion Description: </B>
<code>String</code> methods <code>startsWith</code> and <code>endsWith</code>
support wild card queries. JDO does not define any special semantic to the
argument passed to the method; in particular,
it does not define any wild card characters.

 */

public class StartsWithAndEndsWith extends QueryTest {

    /** */
    private static final String ASSERTION_FAILED = 
        "Assertion A14.6.2-33 (StartsWithAndEndsWith) failed: ";
    
    /**
     * The <code>main</code> is called when the class
     * is directly executed from the command line.
     * @param args The arguments passed to the program.
     */
    public static void main(String[] args) {
        BatchTestRunner.run(StartsWithAndEndsWith.class);
    }
    
    /** */
    public void test() {
        pm = getPM();
                
        try {
            // read test data
            CompanyModelReader reader = 
                loadCompanyModel(pm, "org/apache/jdo/tck/query/company.xml");
            runTest(pm, reader);
        }
        finally {
            cleanupCompanyModel(pm);
            pm.close();
            pm = null;
        }
    }
    
     /** */
    void runTest(PersistenceManager pm, CompanyModelReader reader) {
        Query q;
        Object result;
        Collection expected;
        
        Transaction tx = pm.currentTransaction();
        tx.begin();

        // startsWith
        q = pm.newQuery(Employee.class, "firstname.startsWith(\"emp1\")");
        result = q.execute();
        expected = new HashSet();
        expected.add(reader.getFullTimeEmployee("emp1"));
        checkQueryResultWithoutOrder(ASSERTION_FAILED, result, expected);
            
        // endsWith
        q = pm.newQuery(Employee.class, "firstname.endsWith(\"1First\")");
        result = q.execute();
        expected = new HashSet();
        expected.add(reader.getFullTimeEmployee("emp1"));
        checkQueryResultWithoutOrder(ASSERTION_FAILED, result, expected);
            
        tx.commit();
    }
}