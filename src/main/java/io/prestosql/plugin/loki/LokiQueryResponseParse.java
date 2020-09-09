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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.prestosql.spi.PrestoException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.prestosql.plugin.loki.LokiErrorCode.LOKI_PARSE_ERROR;
import static java.util.Collections.singletonList;

public class LokiQueryResponseParse
{
    private boolean status;

    private String error;
    private String errorType;
    private String resultType;
    private String result;
    private List<LokiMetricResult> results;

    public LokiQueryResponseParse(InputStream response)
            throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        JsonParser parser = new JsonFactory().createParser(response);
        while (!parser.isClosed()) {
            JsonToken jsonToken = parser.nextToken();
            if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                if (parser.getCurrentName().equals("status")) {
                    parser.nextToken();
                    if (parser.getValueAsString().equals("success")) {
                        this.status = true;
                        while (!parser.isClosed()) {
                            parser.nextToken();
                            if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                                if (parser.getCurrentName().equals("resultType")) {
                                    parser.nextToken();
                                    resultType = parser.getValueAsString();
                                }
                                if (parser.getCurrentName().equals("result")) {
                                    parser.nextToken();
                                    ArrayNode node = mapper.readTree(parser);
                                    result = node.toString();
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        //error path
                        String parsedStatus = parser.getValueAsString();
                        parser.nextToken();
                        parser.nextToken();
                        this.errorType = parser.getValueAsString();
                        parser.nextToken();
                        parser.nextToken();
                        error = parser.getValueAsString();
                        throw new PrestoException(LOKI_PARSE_ERROR, "Unable to parse Loki response: " + parsedStatus + " " + errorType + " " + error);
                    }
                }
            }
            if (result != null) {
                break;
            }
        }
        if (result != null && resultType != null) {
            switch (resultType) {
                case "matrix":
                case "streams":
                    results = mapper.readValue(result, new TypeReference<List<LokiMetricResult>>() {});
                    break;
                case "scalar":
                case "string":
                    LokiTimeSeriesValue stringOrScalarResult = mapper.readValue(result, new TypeReference<LokiTimeSeriesValue>() {});
                    Map<String, String> madeUpMetricHeader = new HashMap<>();
                    madeUpMetricHeader.put("__name__", resultType);
                    LokiTimeSeriesValueArray timeSeriesValues = new LokiTimeSeriesValueArray(singletonList(stringOrScalarResult));
                    results = singletonList(new LokiMetricResult(madeUpMetricHeader, timeSeriesValues));
            }
        }
    }

    public String getError()
    {
        return error;
    }

    public String getErrorType()
    {
        return errorType;
    }

    public List<LokiMetricResult> getResults()
    {
        return results;
    }

    public boolean getStatus()
    {
        return this.status;
    }
}
