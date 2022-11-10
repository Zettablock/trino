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
import io.airlift.slice.Slices;
import io.trino.spi.function.Description;
import io.trino.spi.function.ScalarFunction;
import io.trino.spi.function.SqlType;
import io.trino.spi.type.StandardTypes;

import java.math.BigInteger;

public class ConvertHexToBigIntegerStringFunction
{
    private ConvertHexToBigIntegerStringFunction()
    {
    }

    @ScalarFunction("ConvertHexToBigInteger")
    @Description("ConvertHexToBigInteger")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice convertHexToBigInteger(@SqlType(StandardTypes.VARCHAR) Slice slice)
    {
        try {
            String argument = slice.toStringUtf8();
            String result = convertHexToBigIntegerImpl(argument);
            return Slices.utf8Slice(result);
        }
        catch (Throwable e) {
            return Slices.utf8Slice("");
        }
    }

    public static String convertHexToBigIntegerImpl(String argument)
    {
        String digits = "0123456789ABCDEF";
        BigInteger val = new BigInteger("0");
        BigInteger base16 = new BigInteger("16");
        String s = argument;
        if (s.startsWith("0x") || s.startsWith("0X")) {
            s = s.substring(2);
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f') {
                c += 'A' - 'a';
            }
            int d = digits.indexOf(c);
            val = val.multiply(base16).add(new BigInteger(String.valueOf(d)));
        }
        return val.toString();
    }
}
