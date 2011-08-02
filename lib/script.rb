require 'java'
require 'rubygems'
require 'bundler'
Bundler.setup
require 'echolon'

class JRubyRiverModule
  def initialize(riverName, settings, client, threadPool)
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
