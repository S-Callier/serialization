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

package sebastien.callier.serialization.codec.extendable.array;

import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.codec.CodecCache;
import sebastien.callier.serialization.exceptions.MissingCodecException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class StringArrayCodec extends ObjectArrayCodec<String> {

    public StringArrayCodec(
            byte reservedByte,
            CodecCache codecCache) throws MissingCodecException {
        super(reservedByte, String.class, codecCache);
    }

    public StringArrayCodec(
            byte reservedByte,
            Codec<String> elementCodec) {
        super(reservedByte, String.class, elementCodec);
    }

    @Override
    public boolean writes(Class clazz) {
        return String[].class.equals(clazz);
    }
}
