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

package org.opensearch.painless.ir;

import org.opensearch.painless.ClassWriter;
import org.opensearch.painless.Location;
import org.opensearch.painless.MethodWriter;
import org.opensearch.painless.phase.IRTreeVisitor;
import org.opensearch.painless.symbol.WriteScope;

public abstract class IRNode {

    /* ---- begin node data ---- */

    private final Location location;

    public Location getLocation() {
        return location;
    }

    /* ---- end node data, begin visitor ---- */

    public abstract <Scope> void visit(IRTreeVisitor<Scope> irTreeVisitor, Scope scope);
    public abstract <Scope> void visitChildren(IRTreeVisitor<Scope> irTreeVisitor, Scope scope);

    /* ---- end visitor ---- */

    public IRNode(Location location) {
        this.location = location;
    }

    protected abstract void write(ClassWriter classWriter, MethodWriter methodWriter, WriteScope writeScope);

}
