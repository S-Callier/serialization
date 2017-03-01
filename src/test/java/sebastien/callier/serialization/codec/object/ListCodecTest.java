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

package sebastien.callier.serialization.codec.object;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.codec.CodecCache;
import sebastien.callier.serialization.codec.primitive.LongCodec;
import sebastien.callier.serialization.deserializer.Deserializer;
import sebastien.callier.serialization.deserializer.DeserializerFactory;
import sebastien.callier.serialization.exceptions.MissingCodecException;
import sebastien.callier.serialization.serializer.Serializer;
import sebastien.callier.serialization.serializer.SerializerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class ListCodecTest {
    private static final CodecCache cache = new CodecCache();
    private static final ListCodec codec = new ListCodec((byte) 0, cache);
    private static final ListCodec invalidCodec = new ListCodec((byte) 50, cache);
    private static Codec<List<Long>> typed;

    private static SerializerFactory serializerFactory;
    private static DeserializerFactory deserializerFactory;

    @BeforeClass
    public static void prepare() throws MissingCodecException {
        cache.register(codec);
        cache.register(new LongCodec((byte) -50));
        typed = codec.asTypedList(Long.class);
        serializerFactory = new SerializerFactory(cache);
        deserializerFactory = new DeserializerFactory(cache);
    }

    @Test
    public void testListWriteRead() throws IOException {
        testList(null);
        testList(new ArrayList<>());
        List<Object> list = new ArrayList<>();
        for (long i = 0; i < 500; i++) {
            list.add(i);
            testList(list);
        }
    }

    @Test
    public void testInvalidData() throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(new ArrayList<>(), invalidCodec);
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

    private void testList(List<Object> value) throws IOException {
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

    @Test
    public void testListTypedWriteRead() throws IOException {
        testTypedList(null);
        testTypedList(new ArrayList<>());
        List<Long> list = new ArrayList<>();
        for (long i = 0; i < 500; i++) {
            list.add(i);
            testTypedList(list);
        }
    }

    @Test
    public void testInvalidTypedData() throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(new ArrayList<>(), invalidCodec);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            deserializer.read(typed);
            Assert.fail("missing exception");
        } catch (IOException e) {
            //expected
        }
    }

    private void testTypedList(List<Long> value) throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(value, typed);
        serializer.append(value);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            assertThat(deserializer.read(), is(value));
            assertThat(deserializer.read(typed), is(value));
            assertThat(deserializer.available(), is(0));
        }
    }
}
