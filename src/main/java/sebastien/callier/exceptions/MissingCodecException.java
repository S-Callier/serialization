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

package sebastien.callier.exceptions;

import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class MissingCodecException extends IOException {
    private static final long serialVersionUID = 3523949695365280066L;

    public MissingCodecException(Class clazz) {
        super("No registered codec for: " + clazz.getSimpleName());
    }
}