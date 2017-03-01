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

package sebastien.callier.serialization.codec.extendable.object.field;

import sebastien.callier.serialization.deserializer.InputStreamWrapper;
import sebastien.callier.serialization.serializer.OutputStreamWrapper;

import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public interface FieldCodec {

    /**
     * @param wrapper  the output to write
     * @param instance the object containing this field
     * @throws IOException
     */
    void write(
            OutputStreamWrapper wrapper,
            Object instance) throws IOException;

    /**
     * @param wrapper  the input to read from
     * @param instance the instance where to set the field value
     * @throws IOException
     */
    void read(
            InputStreamWrapper wrapper,
            Object instance) throws IOException;
}