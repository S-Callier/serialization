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
import sebastien.callier.serialization.codec.CodecCache;
import sebastien.callier.serialization.codec.array.ByteArrayCodec;
import sebastien.callier.serialization.deserializer.Deserializer;
import sebastien.callier.serialization.serializer.Serializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class StreamWrappingTest {
    @Test
    public void basicSerialization() throws IOException {
        Map map = new HashMap<>(5);
        CodecCache cache = new CodecCache();
        ByteArrayCodec codec = new ByteArrayCodec((byte) 10);
        cache.register(codec);

        byte[] data = getRandomData();

        Serializer serializer = new Serializer(cache);
        serializer.append(data, codec);
        serializer.close();

        Deserializer deserializer = Deserializer.newInstance(
                cache,
                serializer.getByteArray(),
                0,
                serializer.currentSize());
        byte[] deserialized = deserializer.read(codec);

        assertThat(Arrays.equals(data, deserialized), is(true));
    }

    private byte[] getRandomData() {
        byte[] data = new byte[50];
        new Random().nextBytes(data);
        return data;
    }
}
