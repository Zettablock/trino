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

import java.util.Calendar;
import java.util.TimeZone;

public class DatestrFunctions
{
    private DatestrFunctions()
    {
    }

    @ScalarFunction("DatestrFromUnixtime")
    @Description("DatestrFromUnixtime")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice datestrFromUnixtime(@SqlType(StandardTypes.DOUBLE) Double unixtime)
    {
        try {
            String result = datestrFromUnixtimeImpl(unixtime);
            return Slices.utf8Slice(result);
        }
        catch (Throwable e) {
            return Slices.utf8Slice("");
        }
    }

    @ScalarFunction("DatehourFromUnixtime")
    @Description("DatehourFromUnixtime")
    @SqlType(StandardTypes.INTEGER)
    public static Integer datehourFromUnixtime(@SqlType(StandardTypes.DOUBLE) Double unixtime)
    {
        try {
            Integer result = datehourFromUnixtimeImpl(unixtime);
            return result;
        }
        catch (Throwable e) {
            return null;
        }
    }

    public static String datestrFromUnixtimeImpl(Double unixtime)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis((long) (unixtime * 1000));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String result = String.format("%04d-%02d-%02d", year, month, day);
        return result;
    }

    public static Integer datehourFromUnixtimeImpl(Double unixtime)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis((long) (unixtime * 1000));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int result = year * 1000000 + month * 10000 + day * 100 + hour;
        return result;
    }
}
