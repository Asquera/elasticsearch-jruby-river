package com.asquera.elasticsearch.river.jruby;

import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.ThreadContext;
import org.jruby.anno.JRubyMethod;
import static org.jruby.runtime.Visibility.*;

public class KernelMethods {
    @JRubyMethod(name = "river", required = 1, visibility = PRIVATE)
    public static void river(ThreadContext context, IRubyObject recv, IRubyObject object) {
        context.getRuntime().getGlobalVariables().set("$river", object);
    }
}