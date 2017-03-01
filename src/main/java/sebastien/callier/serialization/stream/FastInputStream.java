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

package sebastien.callier.serialization.stream;

import java.io.InputStream;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class FastInputStream extends InputStream {
    private byte bytes[];
    private int mark;

    private int position;

    private int byteCount;
    private int byteOffset;

    public FastInputStream(byte bytes[]) {
        this.bytes = bytes;
        this.byteCount = bytes.length;
    }

    public FastInputStream(
            byte bytes[],
            int offset,
            int length) {
        this.bytes = bytes;
        this.byteOffset = offset;
        this.position = offset;
        this.mark = offset;
        this.byteCount = Math.min(offset + length, bytes.length);
    }

    @Override
    public int read() {
        return (position < byteCount) ? (bytes[position++] & 0xFF) : -1;
    }

    @Override
    public int read(
            byte[] data,
            int offset,
            int length) {
        if (offset < 0 ||
            length < 0 ||
            length + offset > data.length) {
            throw new IndexOutOfBoundsException();
        }
        if (position >= byteCount) {
            return -1;
        }
        length = (position + length > byteCount) ? byteCount - position : length;
        if (length <= 0) {
            return 0;
        }
        System.arraycopy(bytes, position, data, offset, length);
        position += length;
        return length;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readAheadLimit) {
        mark = position;
    }

    @Override
    public void reset() {
        position = mark;
    }

    @Override
    public long skip(long n) {
        if (n <= 0) {
            return 0;
        }
        n = (position + n > byteCount) ? byteCount - position : n;
        position += n;
        return n;
    }

    @Override
    public int available() {
        return byteCount - position;
    }

    @Override
    public void close() {
        //nothing to do
    }

    public int getPosition() {
        return position;
    }

    public int getSize() {
        return byteCount - byteOffset;
    }
}
