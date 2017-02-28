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

package sebastien.callier.codec.object;

import org.junit.BeforeClass;
import org.junit.Test;
import sebastien.callier.codec.Codec;
import sebastien.callier.codec.CodecCache;
import sebastien.callier.codec.primitive.LongCodec;
import sebastien.callier.deserialization.Deserializer;
import sebastien.callier.deserialization.DeserializerFactory;
import sebastien.callier.serialization.Serializer;
import sebastien.callier.serialization.SerializerFactory;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class NullCodecTest {
    private static final Codec<Object> codec = new NullCodec();
    private static final Codec<Long> invalidCodec = new LongCodec((byte) 50);

    private static SerializerFactory serializerFactory;
    private static DeserializerFactory deserializerFactory;

    @BeforeClass
    public static void prepare() {
        CodecCache cache = new CodecCache();
        serializerFactory = new SerializerFactory(cache);
        deserializerFactory = new DeserializerFactory(cache);
    }

    @Test
    public void testNullWriteRead() throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(null, invalidCodec);
        serializer.append(null);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            assertThat(deserializer.read(), nullValue());
            assertThat(deserializer.read(codec), nullValue());
            assertThat(deserializer.available(), is(0));
        }
    }
}
