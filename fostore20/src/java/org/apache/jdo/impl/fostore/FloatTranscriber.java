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
import java.io.DataOutput;
import java.io.IOException;

/**
* Transcribes float values.
*
* @author Dave Bristor
*/
class FloatTranscriber extends FOStoreTranscriber {
    private static FloatTranscriber instance = new FloatTranscriber();

    private FloatTranscriber() {}

    static FloatTranscriber getInstance() {
        return instance;
    }
    
    void storeFloat(float value, DataOutput out) throws IOException {
        out.writeFloat(value);
    }

    float fetchFloat(DataInput in) throws IOException {
        return in.readFloat();
    }

    void skip(DataInput in) throws IOException { 
        in.readFloat(); 
    }
}
