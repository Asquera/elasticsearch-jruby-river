package com.asquera.elasticsearch.plugins.river.jruby;
 
import org.testng.annotations.*;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class JRubyRiverPluginTests {
   @BeforeClass
   public void setUp() {  
     // code that will be invoked when this test is instantiated
   }
   
   @Test
   public void riverTest() throws Exception {
       Node node = NodeBuilder.nodeBuilder().settings(ImmutableSettings.settingsBuilder().put("gateway.type", "none")).node();
       
       node.client().prepareIndex("_river", "test1", "_meta").setSource(jsonBuilder().startObject().field("type", "jruby").endObject()).execute().actionGet();
       
       System.out.println("fooooo");
       Thread.sleep(10000);
   }
 
}
