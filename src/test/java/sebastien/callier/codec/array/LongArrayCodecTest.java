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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sebastien.callier.codec.CodecCache;
import sebastien.callier.deserialization.Deserializer;
import sebastien.callier.deserialization.DeserializerFactory;
import sebastien.callier.serialization.Serializer;
import sebastien.callier.serialization.SerializerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class LongArrayCodecTest {
    private static final Random random = new Random(new SecureRandom().nextLong());

    private static final LongArrayCodec codec = new LongArrayCodec((byte) 0);
    private static final LongArrayCodec invalidCodec = new LongArrayCodec((byte) 50);

    private static SerializerFactory serializerFactory;
    private static DeserializerFactory deserializerFactory;

    @BeforeClass
    public static void prepare() {
        CodecCache cache = new CodecCache();
        cache.register(codec);
        serializerFactory = new SerializerFactory(cache);
        deserializerFactory = new DeserializerFactory(cache);
    }

    @Test
    public void longArray() throws IOException {
        testLongArray(null);
        testLongArray(new long[]{});
        for (int i = 1; i < 200; i++) {
            long[] b = new long[i];
            for (int j = 0; j < i; j++) {
                b[j] = random.nextLong();
            }
            testLongArray(b);
        }
    }

    @Test
    public void testInvalidData() throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(new long[0], invalidCodec);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            deserializer.read(codec);
            Assert.fail("missing exception");
        } catch (IOException e) {
            //expected
        }
    }

    private void testLongArray(long[] value) throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(value, codec);
        serializer.append(value);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            assertThat(deserializer.read(), is(value));
            assertThat(deserializer.read(codec), is(value));
            assertThat(deserializer.available(), is(0));
        }
    }
}