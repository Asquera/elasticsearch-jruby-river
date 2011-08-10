require 'java'
require 'rubygems'

class River
  include_class "org.elasticsearch.common.util.concurrent.EsExecutors"

  attr_accessor :logger, :river_name, :settings, :client, :thread_pool, :config
  
  def initialize(riverName, settings, client, threadPool, logger)
    self.logger      = logger
    self.river_name  = riverName.name()
    self.settings    = settings
    self.config      = settings.settings["config"]
    self.client      = client
    self.thread_pool = thread_pool
  end
  
  def log(level, *args)
    # logger.debug("foo") would crash because of varargs...
    if args.length == 1
      logger.send(level, args.first, [])
    else
      logger.send(level, *args)
    end
  end

  def start
    raise NotImplementedError.new("You must define #start")
  end

  def close
    raise NotImplementedError.new("You must define #close")
  end
end
