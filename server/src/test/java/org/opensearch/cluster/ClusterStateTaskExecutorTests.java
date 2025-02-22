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
 *     http://www.apache.org/licenses/LICENSE-2.0
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

package org.opensearch.cluster;

import org.opensearch.test.OpenSearchTestCase;
import org.opensearch.cluster.ClusterStateTaskExecutor;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;

public class ClusterStateTaskExecutorTests extends OpenSearchTestCase {

    private class TestTask {
        private final String description;

        TestTask(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description == null ? "" : "Task{" + description + "}";
        }
    }

    public void testDescribeTasks() {
        final ClusterStateTaskExecutor<TestTask> executor = (currentState, tasks) -> {
            throw new AssertionError("should not be called");
        };

        assertThat("describes an empty list", executor.describeTasks(Collections.emptyList()), equalTo(""));
        assertThat("describes a singleton list", executor.describeTasks(Collections.singletonList(new TestTask("a task"))),
            equalTo("Task{a task}"));
        assertThat("describes a list of two tasks",
            executor.describeTasks(Arrays.asList(new TestTask("a task"), new TestTask("another task"))),
            equalTo("Task{a task}, Task{another task}"));

        assertThat("skips the only item if it has no description", executor.describeTasks(Collections.singletonList(new TestTask(null))),
            equalTo(""));
        assertThat("skips an item if it has no description",
            executor.describeTasks(Arrays.asList(
                new TestTask("a task"), new TestTask(null), new TestTask("another task"), new TestTask(null))),
            equalTo("Task{a task}, Task{another task}"));
    }
}
