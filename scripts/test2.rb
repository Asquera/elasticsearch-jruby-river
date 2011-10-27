def start
  client.prepareIndex("test2", "type2", "1").setSource("field1", "value1").execute.actionGet
end

def close
  puts "stop"
end