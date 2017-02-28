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

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class FastOutputStreamTest {
    @Test
    public void growsProperly() {
        FastOutputStream out = new FastOutputStream(1);
        assertThat(out.getByteArray().length, is(1));
        assertThat(out.getSize(), is(0));
        out.write(1);
        assertThat(out.getByteArray().length, is(1));
        assertThat(out.getSize(), is(1));
        out.write(1);
        assertThat(out.getSize(), is(2));
        assertThat(out.getByteArray().length, greaterThan(1));
    }

    @Test
    public void growsProperly2() {
        FastOutputStream out = new FastOutputStream(2);
        assertThat(out.getByteArray().length, is(2));
        out.write(new byte[]{1, 2, 3, 4, 5}, 1, 3);
        assertThat(out.getByteArray().length, greaterThan(2));
        assertThat(out.getSize(), is(3));
        assertThat(Arrays.equals(out.asByteArray(), new byte[]{2, 3, 4}), is(true));
    }
}