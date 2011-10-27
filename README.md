# JRuby Rivers for ElasticSearch

This is a meta river that allows you to implement Rivers using JRuby. As an added bonus, allows those rivers to be changed and reloaded at cluster runtime.

## Minor warning

At the moment, this plugin is compiled and tested against ElasticSearch 0.17. It should work with 0.18, though.

## Installation

    bin/plugin install river-jruby -url https://github.com/downloads/Asquera/elasticsearch-jruby-river/elasticsearch-jruby-river-1.1.zip

## Simple Usage

Put this script in `$ES_HOME/scripts/simple.rb`. Be aware that you _must_ implement `start` and `stop`:

    def start
      logger.info "started river [{}]", name
    end

    def close
      logger.info "closed river [{}]", name
    end

And start it like this:

    curl -XPOST localhost:9200/_river/simple/_meta -d '{"type": "jruby"}'
    
Done. Be aware that if no options are given, the river name is used to determine the script name. You cannot start 2 of the same kind using this technique.

You can reload the river by deleting and starting it again:

    curl -XDELETE localhost:9200/_river/simple
    curl -XPOST localhost:9200/_river/simple/_meta -d '{"type": "jruby"}'

Any code changes will be picked up.

## Exposed Settings

The plugin exposes the following properties by defining the `RiverSettings` module. Be aware
that all those (except `name`) are the raw objects given by ElasticSearch. They are also available as global variables:

    class MyClass
      include RiverSettings

      def start
        client #=> The client for the elasticsearch node
        name #=> The name of the river
        settings #=> The settings object
        logger #=> The logger object to use
        threadpool #=> The threadpool that this River can use
      end
    end

    # also: $client, $name, $logger, $threadpool, $settings

Be aware that the `settings` module gives access to both the global cluster settings and to the settings given to the river:

    settings.settings #=> The river configuration
    settings.globalSettings #=> The global configuration

## Advanced usage

### Using a class

Instead of using the toplevel object, you can designate a class to be used. In this case, you have to notify the runtime by calling `river` to ensure that the correct class is picked up. To gain access to the river configuration, include `RiverSettings`:

    class MyRiver
      include RiverSettings

      def start
        #....
      end

      def close
        #....
      end
    end
    
    river MyRiver

### Run a different script

To configure which script is run, you can pass additional parameters:

    curl -XPOST localhost:9200/_river/simple/_meta -d '{
      "type": "jruby"
      "jruby": {
        "script_name": "my_script.rb",
        "script_directory": "/my/script/directory"
      }
    }

This will change the working directory of the JRuby interpreter accordingly and run `my_script.rb` instead.

### Additional Settings

You can pass any number of additional settings:

    curl -XPOST localhost:9200/_river/simple/_meta -d '{
      "type": "jruby"
      "jruby": {
        "script_name": "my_script.rb",
        "script_directory": "/my/script/directory"
      },
      "config": {
        "key": "value"
      }
    }

They will be available using the `settings` variable.

    def start
      settings.settings["config"]["key"]
    end

### Using the client properly

Without digging too much into the elasticsearch client API, here is a short sample of things you can do using the client:

    client.prepareIndex("my_feed", "tweet", 1).setSource("field1", "value1").execute.actionGet
    client.admin.cluster #=> Get the cluster admin client
    client.admin.indices #=> Get the indices admin client

### Using inbuilt ElasticSearch thread management

Most rivers involve running threads. If you want to play nice, use ElasticSearchs Thread management system that does most of the work for you:

    require 'java'

    class MyRiver
      include_class "org.elasticsearch.common.util.concurrent.EsExecutors"
      include RiverSettings
      
      def thread(name, &block)
        EsExecutors.daemonThreadFactory(settings.globalSettings, name).newThread(&block)
      end

      def start
        @greeter = thread do
          puts 'hello'
        end
      end

      def close
        @closed = true
        @greeter.interrupt unless @closed
      end
    end
    
## Developing

Get gradle [gradle.org], and run:

    gradle zip

To test, run:

    gradle zip

Install using:

    bin/plugin install river-jruby -url file://./build/distributions/elasticsearch-jruby-river-<version>.zip

### TODO

* Make the test suite more noisy
* Maybe implement some common patterns
* Documentation on how to use bundler, etc.
