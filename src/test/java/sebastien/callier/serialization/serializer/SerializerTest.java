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

import org.junit.Test;
import sebastien.callier.serialization.codec.CodecCache;
import sebastien.callier.serialization.codec.object.StringCodec;
import sebastien.callier.serialization.exceptions.MissingCodecException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class SerializerTest {
    @Test(expected = MissingCodecException.class)
    public void failsGracefully() throws IOException {
        Date unsupported = new Date();
        try (Serializer serializer = new Serializer(new CodecCache())) {
            serializer.append(unsupported);
        }
    }

    @Test
    public void outputMethodsReturnSameData() throws IOException {
        CodecCache cache = new CodecCache();
        cache.register(new StringCodec(cache.nextFreeMarker(), 0));

        Serializer serializer = new Serializer(cache);
        serializer.append("testString");
        serializer.close();

        byte[] backingArray = serializer.getByteArray();
        int size = serializer.currentSize();
        byte[] copyArray = serializer.asByteArray();
        ByteBuffer byteBuffer = serializer.asByteBuffer();

        assertThat(copyArray.length, is(size));
        assertThat(byteBuffer.remaining(), is(size));

        for (int i = 0; i < size; i++) {
            assertThat(backingArray[i], is(copyArray[i]));
            assertThat(byteBuffer.get(), is(copyArray[i]));
        }
    }
}
