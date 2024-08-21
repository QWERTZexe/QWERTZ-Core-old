package app.qwertz.qwertzcore.impl.toposort;

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


import java.util.ArrayList;
import java.util.List;

public abstract class SortableNode<N extends SortableNode<N>> {
    final List<N> subsequentNodes = new ArrayList<>();
    final List<N> previousNodes = new ArrayList<>();
    boolean visited = false;

    /**
     * @return Description of this node, used to print the cycle warning.
     */
    protected abstract String getDescription();

    public static <N extends SortableNode<N>> void link(N first, N second) {
        if (first == second) {
            throw new IllegalArgumentException("Cannot link a node to itself!");
        }

        first.subsequentNodes.add(second);
        second.previousNodes.add(first);
    }
}