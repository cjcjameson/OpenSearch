/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.search.aggregations.bucket;

import org.opensearch.common.xcontent.XContentParseException;
import org.opensearch.common.xcontent.XContentParser;
import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.search.aggregations.BaseAggregationTestCase;
import org.opensearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.opensearch.search.aggregations.bucket.range.RangeAggregator.Range;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

public class DateRangeTests extends BaseAggregationTestCase<DateRangeAggregationBuilder> {

    @Override
    protected DateRangeAggregationBuilder createTestAggregatorBuilder() {
        int numRanges = randomIntBetween(1, 10);
        DateRangeAggregationBuilder factory = new DateRangeAggregationBuilder(randomAlphaOfLengthBetween(3, 10));
        for (int i = 0; i < numRanges; i++) {
            String key = null;
            if (randomBoolean()) {
                key = randomAlphaOfLengthBetween(1, 20);
            }
            double from = randomBoolean() ? Double.NEGATIVE_INFINITY : randomIntBetween(Integer.MIN_VALUE, Integer.MAX_VALUE - 1000);
            double to = randomBoolean() ? Double.POSITIVE_INFINITY
                    : (Double.isInfinite(from) ? randomIntBetween(Integer.MIN_VALUE, Integer.MAX_VALUE)
                            : randomIntBetween((int) from, Integer.MAX_VALUE));
            if (randomBoolean()) {
                factory.addRange(new Range(key, from, to));
            } else {
                String fromAsStr = Double.isInfinite(from) ? null : String.valueOf(from);
                String toAsStr = Double.isInfinite(to) ? null : String.valueOf(to);
                factory.addRange(new Range(key, fromAsStr, toAsStr));
            }
        }
        factory.field(INT_FIELD_NAME);
        if (randomBoolean()) {
            factory.format("###.##");
        }
        if (randomBoolean()) {
            factory.keyed(randomBoolean());
        }
        if (randomBoolean()) {
            factory.missing(randomIntBetween(0, 10));
        }
        if (randomBoolean()) {
            factory.timeZone(randomZone());
        }
        return factory;
    }

    public void testParsingRangeStrict() throws IOException {
        final String rangeAggregation = "{\n" +
                "\"field\" : \"date\",\n" +
                "\"format\" : \"yyyy-MM-dd\",\n" +
                "\"ranges\" : [\n" +
                "    { \"from\" : \"2017-01-01\", \"to\" : \"2017-01-02\", \"badField\" : \"abcd\" }\n" +
                "]\n" +
            "}";
        XContentParser parser = createParser(JsonXContent.jsonXContent, rangeAggregation);
        XContentParseException ex = expectThrows(XContentParseException.class,
                () -> DateRangeAggregationBuilder.PARSER.parse(parser, "aggregationName"));
        assertThat(ex.getCause(), notNullValue());
        assertThat(ex.getCause().getMessage(), containsString("badField"));
    }

}
