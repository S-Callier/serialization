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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class FastInputStreamTest {
    @Test
    public void emptyReadReturnsMinus1() {
        FastInputStream stream = new FastInputStream(new byte[0]);
        assertThat(stream.getPosition(), is(0));
        assertThat(stream.read(), is(-1));
        assertThat(stream.getPosition(), is(0));
        assertThat(stream.read(new byte[1], 0, 1), is(-1));
        assertThat(stream.getPosition(), is(0));
        assertThat(stream.getSize(), is(0));
    }

    @Test
    public void readAdvancesPosition() {
        FastInputStream stream = new FastInputStream(new byte[]{123, 123, 123, 123, 123});
        assertThat(stream.getPosition(), is(0));
        assertThat(stream.read(), is(123));
        assertThat(stream.getPosition(), is(1));

        byte[] data = new byte[2];
        assertThat(stream.read(data, 0, 1), is(1));
        assertThat(data[0], is((byte) 123));
        assertThat(data[1], is((byte) 0));
        assertThat(stream.getPosition(), is(2));

        data = new byte[3];
        assertThat(stream.read(data, 1, 2), is(2));
        assertThat(data[0], is((byte) 0));
        assertThat(data[1], is((byte) 123));
        assertThat(data[2], is((byte) 123));
        assertThat(stream.getPosition(), is(4));

        assertThat(stream.read(new byte[0], 0, 0), is(0));
        assertThat(stream.getPosition(), is(4));

        assertThat(stream.read(data, 0, 3), is(1));
        assertThat(data[0], is((byte) 123));
        assertThat(stream.getPosition(), is(5));
    }

    @Test
    public void skipping() {
        FastInputStream stream = new FastInputStream(new byte[]{1, 2, 3, 4, 5});

        assertThat(stream.skip(2), is(2L));
        assertThat(stream.getPosition(), is(2));
        assertThat(stream.skip(1234), is(3L));
        assertThat(stream.getPosition(), is(5));
        assertThat(stream.skip(-123), is(0L));
    }

    @Test
    public void marking() {
        FastInputStream stream = new FastInputStream(new byte[]{1, 2, 3, 4, 5});
        assertThat(stream.markSupported(), is(true));
        stream.skip(1L);

        stream.mark(123);
        assertThat(stream.getPosition(), is(1));
        int read = stream.read();

        stream.reset();
        assertThat(stream.getPosition(), is(1));
        int read2 = stream.read();

        stream.reset();
        assertThat(stream.getPosition(), is(1));
        byte[] data = new byte[1];
        stream.read(data, 0, 1);
        int read3 = data[0];

        assertThat(read, not(-1));
        assertThat(read, is(read2));
        assertThat(read, is(read3));
    }

    @Test
    public void initialOffset() {
        FastInputStream stream = new FastInputStream(new byte[]{1, 2, 3, 4, 5}, 2, 3);

        assertThat(stream.getPosition(), is(2));
        stream.reset();
        assertThat(stream.getPosition(), is(2));

        assertThat(stream.getSize(), is(3));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void validateOffset() {
        FastInputStream stream = new FastInputStream(new byte[0]);
        stream.read(new byte[10], -1, 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void validateLength() {
        FastInputStream stream = new FastInputStream(new byte[0]);
        stream.read(new byte[10], 0, -1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void validateRemainingLength() {
        FastInputStream stream = new FastInputStream(new byte[0]);
        stream.read(new byte[10], 9, 5);
    }
}