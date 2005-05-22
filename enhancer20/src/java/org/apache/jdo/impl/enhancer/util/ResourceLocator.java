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

import java.io.InputStream;


/**
 * Provides a method for searching resources.
 */
public interface ResourceLocator
{
    /**
     * Finds a resource with a given name.  This method returns
     * <code>null</code> if no resource with this name is found.
     * The name of a resource is a "/"-separated path name.
     */
    //^olsen: would be better to throw an IOException instead of a
    // RuntimeException but currently not supported by the JDOModel's
    // JavaModel interface
    InputStream getInputStreamForResource(String resourceName);
}
