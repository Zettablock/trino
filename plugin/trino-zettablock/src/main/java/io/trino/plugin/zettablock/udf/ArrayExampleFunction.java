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
package io.trino.plugin.zettablock.udf;

import io.airlift.slice.Slice;
import io.trino.spi.block.Block;
import io.trino.spi.block.BlockBuilder;
import io.trino.spi.function.Description;
import io.trino.spi.function.ScalarFunction;
import io.trino.spi.function.SqlType;
import io.trino.spi.type.StandardTypes;


import static io.trino.spi.type.VarcharType.VARCHAR;

public class ArrayExampleFunction
{
    private ArrayExampleFunction()
    {
    }

    @ScalarFunction("ArrayExample")
    @Description("Example UDF returning Array")
    @SqlType(StandardTypes.ARRAY)
    public static Block arrayExample(@SqlType(StandardTypes.VARCHAR) Slice slice)
    {
        try {
            String argument = slice.toStringUtf8();
            BlockBuilder results = VARCHAR.createBlockBuilder(null, 1, argument.length());
            VARCHAR.writeString(results, argument);
            return results.build();
        }
        catch (Throwable e) {
            BlockBuilder results = VARCHAR.createBlockBuilder(null, 0, 0);
            return results.build();
        }
    }
}
