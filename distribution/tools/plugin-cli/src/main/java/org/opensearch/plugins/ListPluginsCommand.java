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

package org.opensearch.plugins;

import joptsimple.OptionSet;
import org.opensearch.Version;
import org.opensearch.cli.EnvironmentAwareCommand;
import org.opensearch.cli.Terminal;
import org.opensearch.env.Environment;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A command for the plugin cli to list plugins installed in opensearch.
 */
class ListPluginsCommand extends EnvironmentAwareCommand {

    ListPluginsCommand() {
        super("Lists installed opensearch plugins");
    }

    @Override
    protected void execute(Terminal terminal, OptionSet options, Environment env) throws Exception {
        if (Files.exists(env.pluginsFile()) == false) {
            throw new IOException("Plugins directory missing: " + env.pluginsFile());
        }

        terminal.println(Terminal.Verbosity.VERBOSE, "Plugins directory: " + env.pluginsFile());
        final List<Path> plugins = new ArrayList<>();
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(env.pluginsFile())) {
            for (Path plugin : paths) {
                plugins.add(plugin);
            }
        }
        Collections.sort(plugins);
        for (final Path plugin : plugins) {
            printPlugin(env, terminal, plugin, "");
        }
    }

    private void printPlugin(Environment env, Terminal terminal, Path plugin, String prefix) throws IOException {
        terminal.println(Terminal.Verbosity.SILENT, prefix + plugin.getFileName().toString());
        PluginInfo info = PluginInfo.readFromProperties(env.pluginsFile().resolve(plugin));
        terminal.println(Terminal.Verbosity.VERBOSE, info.toString(prefix));
        if (info.getOpenSearchVersion().equals(Version.CURRENT) == false) {
            terminal.errorPrintln(
                "WARNING: plugin ["
                    + info.getName()
                    + "] was built for OpenSearch version "
                    + info.getVersion()
                    + " but version "
                    + Version.CURRENT
                    + " is required"
            );
        }
    }
}
