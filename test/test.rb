require 'java'

Dir["/usr/local/elasticsearch/lib/\*.jar"].each { |jar| require jar }

builder = org.elasticsearch.common.xcontent.XContentFactory;

json = <<-JSON
{
  "type": "jruby",
  "jruby": {
    "script_name": "lib/self_closing_river.rb",
    "load_path": ["/home/vagrant/elasticsearch-jruby-river/river-libs.jar"],
    "ruby_class": "SelfClosingRiver"
  }
}
JSON
settings = org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder.put("cluster.name", "test-cluster-123").put("gateway.type", "none").put("number_of_shards", 1)
node = org.elasticsearch.node.NodeBuilder.nodeBuilder.settings(settings).node;
node.client().prepareIndex("_river", "db", "_meta").setSource(json).execute().actionGet()

sleep(10);

node.close();
