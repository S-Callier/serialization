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

package sebastien.callier.serialization.codec.primitive;

import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.deserializer.InputStreamWrapper;
import sebastien.callier.serialization.serializer.OutputStreamWrapper;

import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class ByteCodec implements Codec<Byte> {
    private final byte byteByte;

    public ByteCodec(byte reservedByte) {
        super();
        byteByte = reservedByte;
    }

    @Override
    public Byte read(InputStreamWrapper wrapper) throws IOException {
        byte marker = wrapper.read1();
        if (marker == NULL) {
            return null;
        }
        switch (marker - byteByte) {
            case 0:
                return wrapper.read1();
            default:
                throw new IOException("Could not deserialize as a byte.");
        }
    }

    @Override
    public void write(OutputStreamWrapper wrapper, Byte value) throws IOException {
        if (value == null) {
            wrapper.writeByte(NULL);
            return;
        }
        wrapper.writeByteAnd1(byteByte, value);
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{byteByte};
    }

    @Override
    public boolean writes(Class clazz) {
        return Byte.class.equals(clazz) || byte.class.equals(clazz);
    }
}
