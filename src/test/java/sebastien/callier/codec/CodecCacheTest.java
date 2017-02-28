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

package sebastien.callier.codec;

import org.junit.Test;
import sebastien.callier.codec.primitive.LongCodec;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class CodecCacheTest {
    @Test
    public void nextFreeMarker() {
        CodecCache cache = new CodecCache();
        assertThat(cache.nextFreeMarker(), is((byte) -127));

        LongCodec longCodec = new LongCodec(cache.nextFreeMarker());
        cache.register(longCodec);

        assertThat(cache.nextFreeMarker(),
                is((byte) (-127 + longCodec.reservedBytes().length)));
    }


}