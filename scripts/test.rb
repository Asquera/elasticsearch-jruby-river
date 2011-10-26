class TestRiver
  def initialize(name, settings, client, *others)
    @name = name.name()
    @client = client
  end

  def start
    @client.prepareIndex("test", "type1", "1").setSource("field1", "value1").execute.actionGet
  end

  def close
    puts "stop"
  end
end

river TestRiver