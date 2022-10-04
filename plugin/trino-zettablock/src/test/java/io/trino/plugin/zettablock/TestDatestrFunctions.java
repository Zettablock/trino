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

import io.trino.plugin.zettablock.udf.DatestrFunctions;
import org.testng.annotations.Test;

import static io.trino.testing.assertions.Assert.assertEquals;

public class TestDatestrFunctions
{
    @Test
    public void testDatestrFromUnixtimeImpl()
    {
        String result = DatestrFunctions.datestrFromUnixtimeImpl(1664582400.000);
        assertEquals(result, "2022-10-01");
    }

    @Test
    public void testDatehourFromUnixtimeImpl()
    {
        Integer result = DatestrFunctions.datehourFromUnixtimeImpl(1664582400.000);
        assertEquals(result, Integer.valueOf(2022100100));

        result = DatestrFunctions.datehourFromUnixtimeImpl(1664582400.000 - 60 * 60);
        assertEquals(result, Integer.valueOf(2022093023));

        result = DatestrFunctions.datehourFromUnixtimeImpl(1664582400.000 - 24 * 60 * 60);
        assertEquals(result, Integer.valueOf(2022093000));
    }
}
