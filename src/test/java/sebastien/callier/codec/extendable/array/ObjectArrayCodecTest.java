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

package sebastien.callier.codec.extendable.array;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sebastien.callier.codec.CodecCache;
import sebastien.callier.codec.object.StringCodec;
import sebastien.callier.deserialization.Deserializer;
import sebastien.callier.deserialization.DeserializerFactory;
import sebastien.callier.exceptions.MissingCodecException;
import sebastien.callier.serialization.Serializer;
import sebastien.callier.serialization.SerializerFactory;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class ObjectArrayCodecTest {
    private static final CodecCache cache = new CodecCache();

    private static final StringCodec stringCodec = new StringCodec((byte) 0, 0);
    private static final StringCodec invalidStringCodec = new StringCodec((byte) 10, 0);
    private static StringArrayCodec codec;
    private static StringArrayCodec invalidCodec;

    private static SerializerFactory serializerFactory;
    private static DeserializerFactory deserializerFactory;

    @BeforeClass
    public static void prepare() throws MissingCodecException {
        cache.register(stringCodec);
        codec = new StringArrayCodec((byte) 50, cache);
        invalidCodec = new StringArrayCodec((byte) 60, invalidStringCodec);
        cache.register(codec);
        serializerFactory = new SerializerFactory(cache);
        deserializerFactory = new DeserializerFactory(cache);
    }

    @Test
    public void testListWriteRead() throws IOException {
        testArray(null);
        testArray(new String[0]);
        testArray(new String[]{null});
        testArray(new String[]{"string",null,"string"});
    }

    @Test
    public void testInvalidData() throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(new String[0], invalidCodec);
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

    private void testArray(String[] value) throws IOException {
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