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

package sebastien.callier.codec.extendable.enumerator;

import sebastien.callier.codec.Codec;
import sebastien.callier.deserialization.InputStreamWrapper;
import sebastien.callier.serialization.OutputStreamWrapper;

import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class EnumCodec<E extends Enum<E>> implements Codec<E> {
    private final byte reservedByte;
    private final Class<E> eClass;

    /**
     * The enumerator should have up to 256 different values for this codec.
     * This is mostly provided as a convenience, but for migration purpose it is usually better
     * to manually attribute ids to the enums values.
     *
     * @param eClass the class of the enumerator
     */
    public EnumCodec(
            byte reservedByte,
            Class<E> eClass) {
        this.reservedByte = reservedByte;
        this.eClass = eClass;
        if (eClass.getEnumConstants().length > 256) {
            throw new IllegalArgumentException(eClass + " has " + eClass.getEnumConstants().length +
                                               " values. Only up to 256 are supported by this codec.");
        }
    }

    @Override
    public E read(InputStreamWrapper wrapper) throws IOException {
        byte marker = wrapper.read1();
        if (marker == NULL) {
            return null;
        }
        switch (marker - reservedByte) {
            case 0:
                return eClass.getEnumConstants()[wrapper.read1() + 128];
            default:
                throw new IOException("Could not deserialize as an Enum.");
        }
    }

    @Override
    public void write(OutputStreamWrapper wrapper, E value) throws IOException {
        if (value == null) {
            wrapper.writeByte(NULL);
            return;
        }
        wrapper.writeByteAnd1(reservedByte, (byte) (value.ordinal() - 128));
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{reservedByte};
    }

    @Override
    public boolean writes(Class clazz) {
        return eClass.equals(clazz);
    }
}
