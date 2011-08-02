/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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

package com.asquera.elasticsearch.river.jruby;

import java.util.Map;

import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.action.bulk.BulkRequestBuilder;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.river.AbstractRiverComponent;
import org.elasticsearch.river.River;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
import org.elasticsearch.threadpool.ThreadPool;

import org.jruby.Ruby;
import org.jruby.javasupport.JavaUtil;
import org.jruby.RubyClass;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.PathType;
import org.jruby.javasupport.util.RuntimeHelpers;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * @author Florian Gilcher (florian.gilcher@asquera.de)
 */
public class JRubyRiver extends AbstractRiverComponent implements River {
    private ScriptingContainer container;
    private IRubyObject river;
    
    @Inject public JRubyRiver(RiverName riverName, RiverSettings settings, Client client, ThreadPool threadPool) throws Exception {        
        super(riverName, settings);
        this.container = new ScriptingContainer();
        System.out.println("here");

        if (settings.settings().containsKey("jruby")) {
            Map<String, Object> riverSettings = (Map<String, Object>) settings.settings().get("jruby");
            
            String className = XContentMapValues.nodeStringValue(riverSettings.get("ruby_class"), "JRubyRiverModule");
            String scriptName = XContentMapValues.nodeStringValue(riverSettings.get("script_name"), "lib/script.rb");

            logger.debug("running script [{}]", scriptName);
            container.runScriptlet(PathType.CLASSPATH, scriptName);
            Ruby runtime = getRuntime();
            RubyClass klass = runtime.getClass(className);
            IRubyObject clientInstance = JavaUtil.convertJavaToRuby(runtime, client);
            IRubyObject threadPoolInstance = JavaUtil.convertJavaToRuby(runtime, threadPool);
            IRubyObject settingsInstance = JavaUtil.convertJavaToRuby(runtime, settings);
            IRubyObject nameInstance = JavaUtil.convertJavaToRuby(runtime, riverName);
            this.river = RuntimeHelpers.invoke(runtime.getCurrentContext(), klass, "new", nameInstance, settingsInstance, clientInstance, threadPoolInstance);
        } else {
            throw new Exception("No options for jruby river found");
        }
    }

    @Override public void start() {
        RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this.river, "start");
    }

    @Override public void close() {
        RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this.river, "close");
    }
    
    private Ruby getRuntime() {
       return container.getProvider().getRuntime();
    }

}
