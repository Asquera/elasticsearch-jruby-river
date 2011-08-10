require 'river'

# A river that does not thread and closes itself when done
class SelfClosingRiver < River

  def start
    run
    self_close
  end
  
  def run
    raise NotImplementedError.new("#run must be implemented by a subclass")
  end
  
  def self_close
    log(:info, "closing river [{}]", river_name)
    cclient.prepareDelete("_river", river_name, "_meta").execute.actionGet
  end

  def close
    
  end
end
