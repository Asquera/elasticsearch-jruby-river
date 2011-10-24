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
import java.util.List;

import java.net.URL;
import java.net.MalformedURLException;

import java.io.File;

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
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaUtil;
import org.jruby.RubyClass;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.PathType;
import org.jruby.embed.AttributeName;
import org.jruby.javasupport.util.RuntimeHelpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.GlobalVariable;
import org.jruby.anno.JRubyMethod;
import static org.jruby.runtime.Visibility.*;
import static org.jruby.CompatVersion.*;

/**
 * @author Florian Gilcher (florian.gilcher@asquera.de)
 */
public class JRubyRiver extends AbstractRiverComponent implements River {
    private ScriptingContainer container;
    private IRubyObject river;
    
    @Inject public JRubyRiver(RiverName riverName, RiverSettings settings, Client client, ThreadPool threadPool) throws Exception {        
        super(riverName, settings);
        this.container = new ScriptingContainer();

        if (settings.settings().containsKey("jruby")) {
            Map<String, Object> riverSettings = (Map<String, Object>)settings.settings().get("jruby");
            
            String className       = XContentMapValues.nodeStringValue(riverSettings.get("ruby_class"), "River");
            String scriptName      = XContentMapValues.nodeStringValue(riverSettings.get("script_name"), "lib/river.rb");
            String scriptDirectory = XContentMapValues.nodeStringValue(riverSettings.get("script_directory"), null);
            
            if (null != scriptDirectory) {
                logger.info("changing for directory [{}]", scriptDirectory);
                container.getLoadPaths().add(scriptDirectory);
                container.setAttribute(AttributeName.BASE_DIR, scriptDirectory);
                container.setCurrentDirectory(scriptDirectory);
            }
            
            Ruby runtime = getRuntime();
            runtime.defineVariable(new GlobalVariable(runtime, "$river", runtime.getNil()));
            RubyModule kernel = runtime.getKernel();
            kernel.defineAnnotatedMethods(JRubyRiver.class);
            
            List<String> loadPath = (List<String>)riverSettings.get("load_path");
            
            if (loadPath != null) {
                for (String path : loadPath) {
                    logger.info(path);
                    container.getLoadPaths().add(path);
                } 
            }

            logger.info("running script [{}]", scriptName);
            container.runScriptlet(PathType.RELATIVE, scriptName);
            logger.info("grabbing class [{}]", className);
            
            RubyClass klass = runtime.getClass(className);
            IRubyObject clientInstance = JavaUtil.convertJavaToRuby(runtime, client);
            IRubyObject threadPoolInstance = JavaUtil.convertJavaToRuby(runtime, threadPool);
            IRubyObject settingsInstance = JavaUtil.convertJavaToRuby(runtime, settings);
            IRubyObject nameInstance = JavaUtil.convertJavaToRuby(runtime, riverName);
            IRubyObject loggerInstance = JavaUtil.convertJavaToRuby(runtime, logger);
            
            this.river = RuntimeHelpers.invoke(runtime.getCurrentContext(), klass, "new", nameInstance, settingsInstance, clientInstance, threadPoolInstance, loggerInstance);
        } else {
            throw new Exception("No options for jruby river found");
        }
    }
    
    private URL getURL(String target) throws MalformedURLException {
        try {
            // First try assuming a protocol is included
            return new URL(target);
        } catch (MalformedURLException e) {
            // Assume file: protocol
            File f = new File(target);
            String path = target;
            if (f.exists() && f.isDirectory() && !path.endsWith("/")) {
                // URLClassLoader requires that directores end with slashes
                path = path + "/";
            }
            return new URL("file", null, path);
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
    
    private RubyClass river() {
        return (RubyClass)getRuntime().getGlobalVariables().get("$river");
    }
    
    @JRubyMethod(name = "river", required = 1, visibility = PRIVATE)
    public static void river(ThreadContext context, IRubyObject recv, IRubyObject object) {
        context.getRuntime().getGlobalVariables().set("$river", object);
    }

}
