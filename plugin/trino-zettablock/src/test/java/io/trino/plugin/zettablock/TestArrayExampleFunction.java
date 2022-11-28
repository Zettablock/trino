/*
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
package io.trino.plugin.zettablock;

import io.airlift.slice.Slice;
import io.airlift.slice.Slices;
import io.trino.plugin.zettablock.udf.ArrayExampleFunction;
import io.trino.plugin.zettablock.udf.ConvertHexToBigIntegerStringFunction;
import io.trino.spi.block.Block;
import io.trino.spi.block.VariableWidthBlock;
import org.testng.annotations.Test;

import static io.trino.testing.assertions.Assert.assertEquals;

public class TestArrayExampleFunction
{
    @Test
    public void testArrayExample()
    {
        Slice slice = Slices.utf8Slice("hello");
        Block block = ArrayExampleFunction.arrayExample(slice);
        assertEquals(VariableWidthBlock.class, block.getClass());
    }
}
