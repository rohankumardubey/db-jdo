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

/*
 * CharIdentityTest.java
 *
 */

package javax.jdo.identity;

import javax.jdo.util.BatchTestRunner;

/**
 *
 * @author clr
 */
public class CharIdentityTest extends SingleFieldIdentityTest {
    
    /** Creates a new instance of CharIdentityTest */
    public CharIdentityTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BatchTestRunner.run(CharIdentityTest.class);
    }
    
    public void testConstructor() {
        CharIdentity c1 = new CharIdentity(Object.class, 'a');
        CharIdentity c2 = new CharIdentity(Object.class, 'a');
        CharIdentity c3 = new CharIdentity(Object.class, 'b');
        assertEquals("Equal CharIdentity instances compare not equal.", c1, c2);
        assertFalse ("Not equal ByteIdentity instances compare equal", c1.equals(c3));
    }
    
    public void testIntConstructor() {
        CharIdentity c1 = new CharIdentity(Object.class, 'a');
        CharIdentity c2 = new CharIdentity(Object.class, 97);
        CharIdentity c3 = new CharIdentity(Object.class, 98);
        assertEquals ("Equal CharIdentity instances compare not equal.", c1, c2);
        assertFalse ("Not equal ByteIdentity instances compare equal", c1.equals(c3));
    }
    
    public void testStringConstructor() {
        CharIdentity c1 = new CharIdentity(Object.class, 'a');
        CharIdentity c2 = new CharIdentity(Object.class, "a");
        CharIdentity c3 = new CharIdentity(Object.class, "b");
        assertEquals ("Equal CharIdentity instances compare not equal.", c1, c2);
        assertFalse ("Not equal ByteIdentity instances compare equal", c1.equals(c3));
    }
    
    public void testStringConstructorTooLong() {
        try {
            CharIdentity c1 = new CharIdentity(Object.class, "ab");
        } catch (IllegalArgumentException iae) {
            return; // good
        }
        fail ("No exception caught for String too long.");
    }
    
    public void testStringConstructorTooShort() {
        try {
            CharIdentity c1 = new CharIdentity(Object.class, "");
        } catch (IllegalArgumentException iae) {
            return; // good
        }
        fail ("No exception caught for String too short.");
    }
    
    public void testCharacterConstructor() {
        CharIdentity c1 = new CharIdentity(Object.class, 'a');
        CharIdentity c2 = new CharIdentity(Object.class, new Character('a'));
        CharIdentity c3 = new CharIdentity(Object.class, new Character('b'));
        assertEquals ("Equal CharIdentity instances compare not equal.", c1, c2);
        assertFalse ("Not equal ByteIdentity instances compare equal", c1.equals(c3));
    }
    
    public void testSerialized() {
        CharIdentity c1 = new CharIdentity(Object.class, 'a');
        CharIdentity c2 = new CharIdentity(Object.class, "a");
        CharIdentity c3 = new CharIdentity(Object.class, "b");
        Object[] scis = writeReadSerialized(new Object[] {c1, c2, c3});
        Object sc1 = scis[0];
        Object sc2 = scis[1];
        Object sc3 = scis[2];
        assertEquals ("Equal CharIdentity instances compare not equal.", c1, sc1);
        assertEquals ("Equal CharIdentity instances compare not equal.", c2, sc2);
        assertEquals ("Equal CharIdentity instances compare not equal.", sc1, c2);
        assertEquals ("Equal CharIdentity instances compare not equal.", sc2, c1);
        assertFalse ("Not equal CharIdentity instances compare equal.", c1.equals(sc3));
        assertFalse ("Not equal CharIdentity instances compare equal.", sc1.equals(c3));
        assertFalse ("Not equal CharIdentity instances compare equal.", sc1.equals(sc3));
        assertFalse ("Not equal CharIdentity instances compare equal.", sc3.equals(sc1));
    }
}
