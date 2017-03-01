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

package sebastien.callier.serialization.serializer;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps output stream to hide byte operators from sensitive eyes.
 *
 * @author Sebastien Callier
 * @since 2017
 */
public final class OutputStreamWrapper implements Closeable {
    private final byte[] cached = new byte[9];

    private final OutputStream out;

    public OutputStreamWrapper(OutputStream out) {
        this.out = out;
    }

    /**
     * Writes a single byte in the output stream
     *
     * @param value the byte
     * @throws IOException
     */
    public void writeByte(byte value) throws IOException {
        out.write(value);
    }

    public void writeBytes(byte[] value) throws IOException {
        out.write(value, 0, value.length);
    }

    public void writeBytes(byte[] value, int length) throws IOException {
        out.write(value, 0, length);
    }

    /**
     * Writes two bytes in the output stream
     *
     * @param byt   the byte
     * @param value the long
     * @throws IOException
     */
    public void writeByteAnd1(byte byt, byte value) throws IOException {
        cached[0] = byt;
        cached[1] = value;
        out.write(cached, 0, 2);
    }

    /**
     * Writes a byte followed by the 2 least significant bytes of an integer in the output stream
     *
     * @param byt   the byte
     * @param value the integer
     * @throws IOException
     */
    public void writeByteAnd2(byte byt, int value) throws IOException {
        cached[0] = byt;
        cached[1] = (byte) ((value >>> 8) & 0xFF);
        cached[2] = (byte) (value & 0xFF);
        out.write(cached, 0, 3);
    }

    /**
     * Writes a byte followed by the 3 least significant bytes of an integer in the output stream
     *
     * @param byt   the byte
     * @param value the integer
     * @throws IOException
     */
    public void writeByteAnd3(byte byt, int value) throws IOException {
        cached[0] = byt;
        cached[1] = (byte) ((value >>> 16) & 0xFF);
        cached[2] = (byte) ((value >>> 8) & 0xFF);
        cached[3] = (byte) (value & 0xFF);
        out.write(cached, 0, 4);
    }

    /**
     * Writes a byte followed by all the 4 bytes of an integer in the output stream
     *
     * @param byt   the byte
     * @param value the integer
     * @throws IOException
     */
    public void writeByteAnd4(byte byt, int value) throws IOException {
        cached[0] = byt;
        cached[1] = (byte) ((value >>> 24) & 0xFF);
        cached[2] = (byte) ((value >>> 16) & 0xFF);
        cached[3] = (byte) ((value >>> 8) & 0xFF);
        cached[4] = (byte) (value & 0xFF);
        out.write(cached, 0, 5);
    }

    /**
     * Writes a byte followed by the 5 least significant bytes of a long in the output stream
     *
     * @param byt   the byte
     * @param value the long
     * @throws IOException
     */
    public void writeByteAnd5(byte byt, long value) throws IOException {
        cached[0] = byt;
        cached[1] = (byte) ((value >>> 32) & 0xFF);
        cached[2] = (byte) ((value >>> 24) & 0xFF);
        cached[3] = (byte) ((value >>> 16) & 0xFF);
        cached[4] = (byte) ((value >>> 8) & 0xFF);
        cached[5] = (byte) (value & 0xFF);
        out.write(cached, 0, 6);
    }

    /**
     * Writes a byte followed by the 6 least significant bytes of a long in the output stream
     *
     * @param byt   the byte
     * @param value the long
     * @throws IOException
     */
    public void writeByteAnd6(byte byt, long value) throws IOException {
        cached[0] = byt;
        cached[1] = (byte) ((value >>> 40) & 0xFF);
        cached[2] = (byte) ((value >>> 32) & 0xFF);
        cached[3] = (byte) ((value >>> 24) & 0xFF);
        cached[4] = (byte) ((value >>> 16) & 0xFF);
        cached[5] = (byte) ((value >>> 8) & 0xFF);
        cached[6] = (byte) (value & 0xFF);
        out.write(cached, 0, 7);
    }

    /**
     * Writes a byte followed by the 7 least significant bytes of a long in the output stream
     *
     * @param byt   the byte
     * @param value the long
     * @throws IOException
     */
    public void writeByteAnd7(byte byt, long value) throws IOException {
        cached[0] = byt;
        cached[1] = (byte) ((value >>> 48) & 0xFF);
        cached[2] = (byte) ((value >>> 40) & 0xFF);
        cached[3] = (byte) ((value >>> 32) & 0xFF);
        cached[4] = (byte) ((value >>> 24) & 0xFF);
        cached[5] = (byte) ((value >>> 16) & 0xFF);
        cached[6] = (byte) ((value >>> 8) & 0xFF);
        cached[7] = (byte) (value & 0xFF);
        out.write(cached, 0, 8);
    }

    /**
     * Writes a byte followed by all the 8 bytes of a long in the output stream
     *
     * @param byt   the byte
     * @param value the long
     * @throws IOException
     */
    public void writeByteAnd8(byte byt, long value) throws IOException {
        cached[0] = byt;
        cached[1] = (byte) ((value >>> 56) & 0xFF);
        cached[2] = (byte) ((value >>> 48) & 0xFF);
        cached[3] = (byte) ((value >>> 40) & 0xFF);
        cached[4] = (byte) ((value >>> 32) & 0xFF);
        cached[5] = (byte) ((value >>> 24) & 0xFF);
        cached[6] = (byte) ((value >>> 16) & 0xFF);
        cached[7] = (byte) ((value >>> 8) & 0xFF);
        cached[8] = (byte) (value & 0xFF);
        out.write(cached, 0, 9);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
