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

package sebastien.callier.serialization.deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class InputStreamWrapper {
    private final byte[] cached = new byte[8];

    private final InputStream input;

    public InputStreamWrapper(InputStream input) {
        this.input = input;
    }

    /**
     * Reads 8 bytes of a long from the input stream
     *
     * @return the long read
     * @throws BufferUnderflowException in case there are not enough bytes available
     * @throws IOException
     */
    public long read8() throws IOException {
        if (input.read(cached, 0, 8) != 8) {
            throw new BufferUnderflowException();
        }
        return (((long) cached[0] & 0xFF) << 56) |
               (((long) cached[1] & 0xFF) << 48) |
               (((long) cached[2] & 0xFF) << 40) |
               (((long) cached[3] & 0xFF) << 32) |
               (((long) cached[4] & 0xFF) << 24) |
               (((long) cached[5] & 0xFF) << 16) |
               (((long) cached[6] & 0xFF) << 8) |
               ((long) cached[7] & 0xFF);
    }

    /**
     * Reads 7 bytes of a long from the input stream
     *
     * @return the long read
     * @throws BufferUnderflowException in case there are not enough bytes available
     * @throws IOException
     */
    public long read7() throws IOException {
        if (input.read(cached, 0, 7) != 7) {
            throw new BufferUnderflowException();
        }
        long raw = (((long) cached[0] & 0xFF) << 48) |
                   (((long) cached[1] & 0xFF) << 40) |
                   (((long) cached[2] & 0xFF) << 32) |
                   (((long) cached[3] & 0xFF) << 24) |
                   (((long) cached[4] & 0xFF) << 16) |
                   (((long) cached[5] & 0xFF) << 8) |
                   ((long) cached[6] & 0xFF);
        return (raw << 8) >> 8;
    }

    /**
     * Reads 6 bytes of a long from the input stream
     *
     * @return the long read
     * @throws BufferUnderflowException in case there are not enough bytes available
     * @throws IOException
     */
    public long read6() throws IOException {
        if (input.read(cached, 0, 6) != 6) {
            throw new BufferUnderflowException();
        }
        long raw = (((long) cached[0] & 0xFF) << 40) |
                   (((long) cached[1] & 0xFF) << 32) |
                   (((long) cached[2] & 0xFF) << 24) |
                   (((long) cached[3] & 0xFF) << 16) |
                   (((long) cached[4] & 0xFF) << 8) |
                   ((long) cached[5] & 0xFF);
        return (raw << 16) >> 16;
    }

    /**
     * Reads 5 bytes of a long from the input stream
     *
     * @return the long read
     * @throws BufferUnderflowException in case there are not enough bytes available
     * @throws IOException
     */
    public long read5() throws IOException {
        if (input.read(cached, 0, 5) != 5) {
            throw new BufferUnderflowException();
        }
        long raw = (((long) cached[0] & 0xFF) << 32) |
                   (((long) cached[1] & 0xFF) << 24) |
                   (((long) cached[2] & 0xFF) << 16) |
                   (((long) cached[3] & 0xFF) << 8) |
                   ((long) cached[4] & 0xFF);
        return (raw << 24) >> 24;
    }

    /**
     * Reads 4 bytes of an integer from the input stream
     *
     * @return the integer read
     * @throws BufferUnderflowException in case there are not enough bytes available
     * @throws IOException
     */
    public int read4() throws IOException {
        if (input.read(cached, 0, 4) != 4) {
            throw new BufferUnderflowException();
        }
        return ((cached[0] & 0xFF) << 24) |
               ((cached[1] & 0xFF) << 16) |
               ((cached[2] & 0xFF) << 8) |
               (cached[3] & 0xFF);
    }

    /**
     * Reads 3 bytes of an integer from the input stream
     *
     * @return the integer read
     * @throws BufferUnderflowException in case there are not enough bytes available
     * @throws IOException
     */
    public int read3() throws IOException {
        if (input.read(cached, 0, 3) != 3) {
            throw new BufferUnderflowException();
        }
        int raw = ((cached[0] & 0xFF) << 16) |
                  ((cached[1] & 0xFF) << 8) |
                  (cached[2] & 0xFF);
        return (raw << 8) >> 8;
    }

    /**
     * Reads 2 bytes of a short from the input stream
     *
     * @return the short read
     * @throws BufferUnderflowException in case there are not enough bytes available
     * @throws IOException
     */
    public short read2() throws IOException {
        if (input.read(cached, 0, 2) != 2) {
            throw new BufferUnderflowException();
        }
        return (short) (((cached[0] & 0xFF) << 8) |
                        cached[1] & 0xFF);
    }

    /**
     * Reads 1 byte from the input stream
     *
     * @return the byte read
     * @throws BufferUnderflowException in case there are not enough bytes available
     * @throws IOException
     */
    public byte read1() throws IOException {
        if (input.read(cached, 0, 1) != 1) {
            throw new BufferUnderflowException();
        }
        return cached[0];
    }

    /**
     * Peeks 1 byte from the input stream, it will be available for the next read.
     *
     * @return the byte read
     * @throws BufferUnderflowException in case there are not enough bytes available
     * @throws IOException
     */
    public byte peek() throws IOException {
        input.mark(1);
        if (input.read(cached, 0, 1) != 1) {
            throw new BufferUnderflowException();
        }
        input.reset();
        return cached[0];
    }

    /**
     * Peeks length bytes from the input stream
     *
     * @param length the number of bytes to read
     * @return the bytes read
     * @throws BufferUnderflowException in case there are not enough bytes available
     * @throws IOException
     */
    public byte[] readBytes(int length) throws IOException {
        byte[] data = new byte[length];
        if (input.read(data, 0, length) != length) {
            throw new BufferUnderflowException();
        }
        return data;
    }
}
