require 'java'

Dir["/usr/local/elasticsearch/lib/\*.jar"].each { |jar| require jar }

builder = org.elasticsearch.common.xcontent.XContentFactory;

folder = File.join(Dir.pwd, "examples")

json = <<-JSON
{
  "type": "jruby",
  "jruby": {
    "script_name": "self_closing_river.rb",
    "script_directory": "#{folder}",
    "ruby_class": "SelfClosingRiver"
  }
}
JSON

settings = org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder.put("cluster.name", "test-cluster-123").put("gateway.type", "none").put("number_of_shards", 1)
node = org.elasticsearch.node.NodeBuilder.nodeBuilder.settings(settings).node;
node.client().prepareIndex("_river", "db", "_meta").setSource(json).execute().actionGet()

sleep(10);

node.close();
