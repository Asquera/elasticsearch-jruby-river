package com.asquera.elasticsearch.plugins.river.jruby;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.river.RiversModule;
import org.elasticsearch.common.inject.AbstractModule;

import com.asquera.elasticsearch.river.jruby.JRubyRiverModule;

/**
 * @author Florian Gilcher (florian.gilcher@asquera.de)
 */
public class JRubyRiverPlugin extends AbstractPlugin { 
    @Inject public JRubyRiverPlugin() {
    }

    @Override public String name() {
        return "river-jruby";
    }

    @Override public String description() {
        return "JRuby River Plugin";
    }
    
    @Override public void processModule(Module module) {
        if (module instanceof RiversModule) {
            ((RiversModule) module).registerRiver("jruby", JRubyRiverModule.class);
        }
    }
}
