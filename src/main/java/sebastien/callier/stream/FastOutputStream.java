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

package sebastien.callier.stream;

import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class FastOutputStream extends OutputStream {
    private final static int INITIAL_SIZE = 64;

    private byte bytes[];
    /**
     * The number of bytes actually written in {@link #bytes}
     */
    private int size;

    /**
     * Creates a new output stream
     *
     * @param size the initial size.
     */
    public FastOutputStream(int size) {
        bytes = new byte[size];
    }

    /**
     * Creates a new output stream of initial size {@link #INITIAL_SIZE}
     */
    public FastOutputStream() {
        this(INITIAL_SIZE);
    }

    @Override
    public void write(int data) {
        int requiredSize = size + 1;
        if (requiredSize > bytes.length) {
            growAtLeast(requiredSize);
        }
        bytes[size] = (byte) data;
        size = requiredSize;
    }

    @Override
    public void write(
            byte data[],
            int from,
            int length) {
        if (from < 0 || length < 0 || from + length > data.length) {
            throw new IndexOutOfBoundsException();
        } else if (length == 0) {
            return;
        }
        int requiredSize = size + length;
        if (requiredSize > bytes.length) {
            growAtLeast(requiredSize);
        }
        System.arraycopy(data, from, bytes, size, length);
        size = requiredSize;
    }

    @Override
    public void flush() {
        //nothing to do
    }

    @Override
    public void close() {
        //nothing to do
    }

    /**
     * Grows the backing array to at least requiredSize.
     *
     * @param requiredSize minimum size
     */
    private void growAtLeast(int requiredSize) {
        bytes = Arrays.copyOf(bytes, Math.max(bytes.length << 1, requiredSize));
    }

    /**
     * Returns a copy of the backing array with the minimum required size.
     * Consider using {@link #getByteArray()} when possible.
     *
     * @return the data written so far
     */
    public byte[] asByteArray() {
        return Arrays.copyOf(bytes, size);
    }

    /**
     * Returns the backing byte array as is.
     * Its size will most likely be larger than the number of valid bytes in the buffer.
     * Please check {@link #getSize()}
     *
     * @return the byte array
     */
    public byte[] getByteArray() {
        return bytes;
    }

    /**
     * @return the actual number of byte in the output stream
     */
    public int getSize() {
        return size;
    }
}

