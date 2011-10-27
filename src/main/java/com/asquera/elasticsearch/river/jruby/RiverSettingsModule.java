package com.asquera.elasticsearch.river.jruby;

import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.ThreadContext;
import org.jruby.anno.JRubyMethod;
import static org.jruby.runtime.Visibility.*;
import org.jruby.anno.JRubyModule;
import org.jruby.RubyObject;

@JRubyModule(name = "RiverSettings")
public class RiverSettingsModule {
    @JRubyMethod
    public static IRubyObject settings(ThreadContext context, IRubyObject self) {
        return context.getRuntime().getGlobalVariables().get("$settings");
    }
    
    @JRubyMethod
    public static IRubyObject name(ThreadContext context, IRubyObject self) {
        return context.getRuntime().getGlobalVariables().get("$name");
    }
    
    @JRubyMethod
    public static IRubyObject client(ThreadContext context, IRubyObject self) {
        return context.getRuntime().getGlobalVariables().get("$client");
    }
    
    @JRubyMethod
    public static IRubyObject logger(ThreadContext context, IRubyObject self) {
        return context.getRuntime().getGlobalVariables().get("$logger");
    }
    
    @JRubyMethod
    public static IRubyObject threadpool(ThreadContext context, IRubyObject self) {
        return context.getRuntime().getGlobalVariables().get("$threadpool");
    }
}