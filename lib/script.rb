require 'java'

class JRubyRiverModule
  def initialize(riverName, settings, client, threadPool)
    puts "here"
    @riverName  = riverName
    @settings   = settings
    @client     = client
    @threadPool = threadPool
  end
  
  def start
    puts "start"
  end
  
  def close
    puts "stop"
  end
end
