package app.qwertz.qwertzcore.command;

/*
   QWERTZ Core side note: The following file was fetched from the Fabric API Repository on GitHub (21/08/2024)

   The File itself belongs to FabricMC and their respective License at that time (See below)
   The File itself was NOT MODIFIED! The Only thing which was edited are the imports and the package name.
   (Everything above this notice)
*/

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import net.minecraft.util.Identifier;

/**
 * Fabric extension to {@link net.minecraft.command.EntitySelectorReader}, implemented
 * using interface injection. This allows custom entity selectors to
 * set a custom flag to a reader. This can be used to implement mutually-exclusive
 * or non-repeatable entity selector option.
 */
public interface FabricEntitySelectorReader {
    /**
     * Sets a flag.
     * @param key the key of the flag
     * @param value the value of the flag
     */
    default void setCustomFlag(Identifier key, boolean value) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    /**
     * Gets the value of the flag.
     * @param key the key of the flag
     * @return the value, or {@code false} if the flag is not set
     */
    default boolean getCustomFlag(Identifier key) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }
}