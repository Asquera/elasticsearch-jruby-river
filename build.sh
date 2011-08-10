#mkdir -p com/asquera/elasticsearch/plugins
#jrubyc --verbose --java lib/JRubyRiverPlugin.rb
bundle install --path gems
cd src
jar cf ../river-jruby.jar es-plugin.properties com/asquera/* 
cd ..
jar cf script.jar lib/*
mkdir -p plugins/river-jruby
cp river-jruby.jar jruby-complete-1.6.3.jar plugins/river-jruby
jar cf river-gems.jar -C gems/jruby/1.8/ .
zip river-jruby.zip jruby-complete-1.6.3.jar river-jruby.jar