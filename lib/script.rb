require 'java'
require 'rubygems'
require 'bundler'
Bundler.setup
require 'echolon'

class JRubyRiverModule
  include_class "org.elasticsearch.common.util.concurrent.EsExecutors"

  def initialize(riverName, settings, client, threadPool)
    @riverName  = riverName
    @settings   = settings
    @client     = client
    @threadPool = threadPool
  end

  def start
    @test_thread = EsExecutors.daemonThreadFactory(@settings.globalSettings, "test_thread").newThread do
      while(true && !@closed) do 
        puts "start"
	sleep 0.1
      end 
    end
    @test_thread.start
  end

  def close
    unless @closed
      @test_thread.interrupt
      @closed = true
    end
  end
end
