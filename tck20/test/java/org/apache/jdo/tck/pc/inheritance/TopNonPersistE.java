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
 

package org.apache.jdo.tck.pc.inheritance;

/** */
public class TopNonPersistE extends TopNonPersistD {  // persistent
    
    public float floatE;  // transactional
    
    public TopNonPersistE () {
        floatE = -4.4f;
    }
    
    public TopNonPersistE (int intA, double doubleB, int intB, char charC, boolean booleanD, float floatE) {
        super(intA, doubleB, intB, charC, booleanD);
        this.floatE = floatE;
    }
}
