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

package sebastien.callier.codec.array;

import sebastien.callier.codec.Codec;
import sebastien.callier.codec.primitive.DoubleCodec;
import sebastien.callier.deserialization.InputStreamWrapper;
import sebastien.callier.serialization.OutputStreamWrapper;
import sebastien.callier.utils.CodecUtils;

import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class DoubleArrayCodec implements Codec<double[]> {
    private final static DoubleCodec doubleCodec = new DoubleCodec((byte) 0);
    private final byte size1;

    public DoubleArrayCodec(byte reservedByte) {
        super();
        size1 = reservedByte;
    }

    @Override
    public double[] read(InputStreamWrapper wrapper) throws IOException {
        Integer size = CodecUtils.readSize(wrapper, size1);
        if (size == null) {
            return null;
        }
        double[] array = new double[size];
        for (int i = 0; i < size; i++) {
            array[i] = doubleCodec.read(wrapper);
        }
        return array;
    }

    @Override
    public void write(
            OutputStreamWrapper wrapper,
            double[] array) throws IOException {
        if (array == null) {
            wrapper.writeByte(NULL);
            return;
        }
        CodecUtils.writeSize(wrapper, array.length, size1);
        for (double element : array) {
            doubleCodec.write(wrapper, element);
        }
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{size1, (byte) (size1 + 1), (byte) (size1 + 2), (byte) (size1 + 3)};
    }

    @Override
    public boolean writes(Class clazz) {
        return double[].class.equals(clazz);
    }
}