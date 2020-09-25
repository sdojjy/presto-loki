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
package io.prestosql.plugin.loki;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;
import io.airlift.slice.XxHash64;
import io.prestosql.plugin.loki.selector.Filters;
import io.prestosql.plugin.loki.selector.TagFilter;
import io.prestosql.spi.function.Description;
import io.prestosql.spi.function.IsNull;
import io.prestosql.spi.function.LiteralParameter;
import io.prestosql.spi.function.LiteralParameters;
import io.prestosql.spi.function.ScalarFunction;
import io.prestosql.spi.function.ScalarOperator;
import io.prestosql.spi.function.SqlNullable;
import io.prestosql.spi.function.SqlType;
import io.prestosql.spi.type.StandardTypes;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.prestosql.spi.function.OperatorType.CAST;
import static io.prestosql.spi.function.OperatorType.EQUAL;
import static io.prestosql.spi.function.OperatorType.GREATER_THAN;
import static io.prestosql.spi.function.OperatorType.GREATER_THAN_OR_EQUAL;
import static io.prestosql.spi.function.OperatorType.HASH_CODE;
import static io.prestosql.spi.function.OperatorType.INDETERMINATE;
import static io.prestosql.spi.function.OperatorType.IS_DISTINCT_FROM;
import static io.prestosql.spi.function.OperatorType.LESS_THAN;
import static io.prestosql.spi.function.OperatorType.LESS_THAN_OR_EQUAL;
import static io.prestosql.spi.function.OperatorType.NOT_EQUAL;
import static io.prestosql.spi.function.OperatorType.XX_HASH_64;

public class LabelFunction {
    private LabelFunction() {
    }

    @Description("Determine whether source starts with prefix or not")
    @ScalarFunction("label_selector")
    @LiteralParameters({"x", "y"})
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean labelSelector(@SqlType("varchar(x)") Slice source, @SqlType("varchar(y)") Slice expr) {
        String v = new String(source.getBytes(), StandardCharsets.UTF_8);
        JSONObject json = JSON.parseObject(v);
        Map<String, String> vmap = new HashMap<>();
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            vmap.put(entry.getKey(), entry.getValue().toString());
        }
        String f = new String(expr.getBytes(), StandardCharsets.UTF_8);
        TagFilter filter = cached.computeIfAbsent(f, (k) -> Filters.parse(f));
        return filter.eval(vmap);
    }

    private static final Map<String, TagFilter> cached = new ConcurrentHashMap<>();

    @Description("Mongodb ObjectId")
    @ScalarFunction
    @SqlType("label")
    public static Slice label() {
        return Slices.wrappedBuffer(new byte[12]);
    }

    @Description("Label from the given string")
    @ScalarFunction
    @SqlType("label")
    public static Slice label(@SqlType(StandardTypes.VARCHAR) Slice value) {
        return value;
    }

    @ScalarOperator(CAST)
    @LiteralParameters("x")
    @SqlType("varchar(x)")
    public static Slice castToVarchar(@LiteralParameter("x") long x, @SqlType("label") Slice value) {
        return value;
    }

    @ScalarOperator(EQUAL)
    @SqlType(StandardTypes.BOOLEAN)
    @SqlNullable
    public static Boolean equal(@SqlType("label") Slice left, @SqlType("label") Slice right) {
        return true;
    }


    @ScalarOperator(IS_DISTINCT_FROM)
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean isDistinctFrom(@SqlType("label") Slice left, @IsNull boolean leftNull, @SqlType("label") Slice right, @IsNull boolean rightNull) {
        if (leftNull != rightNull) {
            return true;
        }
        if (leftNull) {
            return false;
        }
        return notEqual(left, right);
    }

    @ScalarOperator(NOT_EQUAL)
    @SqlType(StandardTypes.BOOLEAN)
    @SqlNullable
    public static Boolean notEqual(@SqlType("label") Slice left, @SqlType("label") Slice right) {
        return !left.equals(right);
    }

    @ScalarOperator(GREATER_THAN)
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean greaterThan(@SqlType("label") Slice left, @SqlType("label") Slice right) {
        return true;
    }

    @ScalarOperator(GREATER_THAN_OR_EQUAL)
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean greaterThanOrEqual(@SqlType("label") Slice left, @SqlType("label") Slice right) {
        return compareTo(left, right) >= 0;
    }

    @ScalarOperator(LESS_THAN)
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean lessThan(@SqlType("label") Slice left, @SqlType("label") Slice right) {
        return compareTo(left, right) < 0;
    }

    @ScalarOperator(LESS_THAN_OR_EQUAL)
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean lessThanOrEqual(@SqlType("label") Slice left, @SqlType("label") Slice right) {
        return compareTo(left, right) <= 0;
    }

    @ScalarOperator(HASH_CODE)
    @SqlType(StandardTypes.BIGINT)
    public static long hashCode(@SqlType("label") Slice value) {
        return value.hashCode();
    }

    private static int compareTo(Slice left, Slice right) {
        return 0;
    }

    @ScalarOperator(INDETERMINATE)
    @SqlType(StandardTypes.BOOLEAN)
    public static boolean indeterminate(@SqlType("label") Slice value, @IsNull boolean isNull) {
        return isNull;
    }

    @ScalarOperator(XX_HASH_64)
    @SqlType(StandardTypes.BIGINT)
    public static long xxHash64(@SqlType("label") Slice value) {
        return XxHash64.hash(value);
    }
}
