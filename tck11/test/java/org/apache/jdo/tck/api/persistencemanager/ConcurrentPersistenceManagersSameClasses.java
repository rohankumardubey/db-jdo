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


package org.apache.jdo.tck.api.persistencemanager;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.jdo.tck.JDO_Test;
import org.apache.jdo.tck.pc.mylib.PCPoint;
import org.apache.jdo.tck.pc.mylib.PCRect;
import org.apache.jdo.tck.util.BatchTestRunner;

/**
 *<B>Title:</B> Same Classes with Concurrent Persistence Managers
 *<BR>
 *<B>Keywords:</B> concurrency multipleJDOimpls
 *<BR>
 *<B>Assertion ID:</B> A5.2-2.
 *<BR>
 *<B>Assertion Description: </B>
The same classes must be supported concurrently by multiple
<code>PersistenceManager</code>s from different implementations.

 */

public class ConcurrentPersistenceManagersSameClasses extends JDO_Test {

    /** */
    private static final String ASSERTION_FAILED = 
        "Assertion A5.2-2 (ConcurrentPersistenceManagersSameClasses) failed: ";
    
    /**
     * The <code>main</code> is called when the class
     * is directly executed from the command line.
     * @param args The arguments passed to the program.
     */
    public static void main(String[] args) {
        BatchTestRunner.run(ConcurrentPersistenceManagersSameClasses.class);
    }
    
    /** */
    public void test() {
    	Properties pmfProperties = loadPMF2Properties();
        PersistenceManagerFactory pmf2 = JDOHelper.getPersistenceManagerFactory(pmfProperties);
        PersistenceManager pm2 = pmf2.getPersistenceManager();
        Transaction tx2 = pm2.currentTransaction();
        
        pm = getPM();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            tx2.begin();
            
            PCPoint p11 = new PCPoint(110, 120);
            PCPoint p12 = new PCPoint(120, 140);
            PCRect rect1 = new PCRect (0, p11, p12);
            pm.makePersistent (rect1);
            
            PCPoint p21 = new PCPoint(210, 220);
            PCPoint p22 = new PCPoint(220, 240);
            PCRect rect2 = new PCRect (0, p21, p22);
            pm2.makePersistent (rect2);
            
            tx.commit();
            tx2.commit();
        
            tx.begin();
            tx2.begin();
            
            PCPoint p11a = findPoint (pm, 110, 120);
            if (p11a != p11) {
                fail(ASSERTION_FAILED, 
                     "unexpected PCPoint instance, expected: 110, 120, found: " + p11a.getX() + ", " + p11a.getY());
            }
            
            PCPoint p21a = findPoint (pm2, 210, 220);
            if (p21a != p21) {
                fail(ASSERTION_FAILED, 
                     "unexpected PCPoint instance, expected: 210, 220, found: " + p21a.getX() + ", " + p21a.getY());
            }
            
            tx.commit();
            tx = null;
            tx2.commit();
            tx2 = null;
        }
        finally {
            cleanupPM(pm);
            pm = null;
            cleanupPM(pm2);
            pm2 = null;
            pmf2.close();
        }
    }

    /** */
    private Properties loadPMF2Properties() {
        String PMF2 = System.getProperty ("PMF2Properties", "jdori2.properties");
        if (debug) logger.debug("Got PMF2Properties file name:" + PMF2);
        Properties ret = loadProperties (PMF2);
        if (debug) logger.debug("Got PMF2Properties: " + ret);
        return ret;
    }

    /** */
    private PCPoint findPoint (PersistenceManager pm, int x, int y) {
        Query q = pm.newQuery (PCPoint.class);
        q.declareParameters ("int px, int py");
        q.setFilter ("x == px & y == py");
        Collection results = (Collection)q.execute (new Integer(x), new Integer(y));
        Iterator it = results.iterator();
        PCPoint ret = (PCPoint)it.next();
        return ret;
    }
}