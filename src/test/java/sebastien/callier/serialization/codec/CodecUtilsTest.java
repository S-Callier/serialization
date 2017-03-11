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

package sebastien.callier.serialization.codec;

import org.junit.Test;
import sebastien.callier.serialization.deserializer.InputStreamWrapper;
import sebastien.callier.serialization.serializer.OutputStreamWrapper;
import sebastien.callier.serialization.utils.CodecUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * @author Sebastien Callier
 * @since 2017
 */
public class CodecUtilsTest {
    @Test
    public void testWideRange() throws IOException {
        for (int value = 1; value > 0; value *= 2) {
            testSize(value - 1);
            testSize(value);
            testSize(value + 1);
        }
        //Test around the the tipping points
        int[] values = new int[]{128, 256, 33024, 65772, 8454380, 16842988};
        for (int value : values) {
            testSize(value - 1);
            testSize(value);
            testSize(value + 1);
        }
    }

    private void testSize(int value) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWrapper wrapper = new OutputStreamWrapper(outputStream);
        CodecUtils.writeSize(wrapper, value, (byte) 0);

        ByteArrayInputStream input = new ByteArrayInputStream(outputStream.toByteArray());
        InputStreamWrapper reader = new InputStreamWrapper(input);
        assertThat(CodecUtils.readSize(reader, (byte) 0), is(value));

        assertThat(input.available(), is(0));
    }
}