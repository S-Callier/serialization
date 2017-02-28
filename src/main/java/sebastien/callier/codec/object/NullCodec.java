/*
 * Copyright 2017 Sebastien Callier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sebastien.callier.codec.object;

import sebastien.callier.codec.Codec;
import sebastien.callier.deserialization.InputStreamWrapper;
import sebastien.callier.serialization.OutputStreamWrapper;

import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class NullCodec implements Codec<Object> {
    public NullCodec() {
        super();
    }

    @Override
    public Object read(InputStreamWrapper wrapper) throws IOException {
        if (wrapper.read1() != NULL) {
            throw new IOException("Could not deserialize as null.");
        }
        return null;
    }

    @Override
    public void write(OutputStreamWrapper wrapper, Object value) throws IOException {
        wrapper.writeByte(NULL);
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{NULL};
    }

    @Override
    public boolean writes(Class clazz) {
        return false;
    }
}
