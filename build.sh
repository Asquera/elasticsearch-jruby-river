#mkdir -p com/asquera/elasticsearch/plugins
#jrubyc --verbose --java lib/JRubyRiverPlugin.rb
javac -classpath "src:jruby-complete-1.6.3.jar:/usr/local/elasticsearch/lib/*" -s src src/com/asquera/elasticsearch/river/jruby/JRubyRiverModule.java src/com/asquera/elasticsearch/plugins/river/jruby/JRubyRiverPlugin.java src/com/asquera/elasticsearch/river/jruby/JRubyRiverModule.java
cd src
jar cf ../river-jruby.jar es-plugin.properties com/asquera/* 
cd ..
jar cf script.jar lib/*
mkdir -p plugins/river-jruby
cp *.jar plugins/river-jruby
zip river-jruby.zip jruby-complete-1.6.3.jar river-jruby.jar script.jar
