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


package org.apache.jdo.impl.enhancer.util;


/**
 * Support for signalling internal implementation errors.
 */
public class Assertion {

    static protected final void affirm(boolean condition) {
        if (!condition)
            throw new InternalError("assertion failed.");
    }

    static protected final void affirm(boolean condition, String msg) {
        if (!condition)
            throw new InternalError("assertion failed: " + msg);
    }

    static protected final void affirm(Object object) {
        if (object == null)
            throw new InternalError("assertion failed.");
    }

    static protected final void affirm(Object object, String msg) {
        if (object == null)
            throw new InternalError("assertion failed: " + msg);
    }
}