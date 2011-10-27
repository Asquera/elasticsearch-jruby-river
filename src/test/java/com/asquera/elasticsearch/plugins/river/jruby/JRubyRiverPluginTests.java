package com.asquera.elasticsearch.plugins.river.jruby;
 
import org.testng.annotations.*;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.action.get.GetResponse;
import java.util.Map;
import java.io.File;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class JRubyRiverPluginTests {
   @Test
   public void testRiver() throws Exception {
       Node node = NodeBuilder.nodeBuilder().node();
       
       node.client().prepareIndex("_river", "test", "_meta").setSource(jsonBuilder().startObject().field("type", "jruby").endObject()).execute().actionGet();

       Thread.sleep(30000);

       GetResponse response = node.client().prepareGet("test", "type1", "1").execute().actionGet();
       
       Map<String, Object> sourceMap = response.sourceAsMap();
       
       assert "value1".equals(sourceMap.get("field1"));
   }
 
}
