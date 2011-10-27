class TestRiver
  include RiverSettings
  
  def start
    client.prepareIndex("test", "type1", "1").setSource("field1", "value1").execute.actionGet
  end

  def close
    puts "stop"
  end
end

river TestRiver