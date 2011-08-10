desc "get jruby gems"
task :"jruby-gems" do
  begin
    require 'jruby-gems'
  rescue LoadError
    warn "Please install jruby-gems"
    exit 1
  end
end

desc "build java parts"
task :build do
  system('javac -classpath "src:jruby-complete-1.6.3.jar:/usr/local/elasticsearch/lib/*" -s src src/com/asquera/elasticsearch/river/jruby/JRubyRiverModule.java src/com/asquera/elasticsearch/plugins/river/jruby/JRubyRiverPlugin.java src/com/asquera/elasticsearch/river/jruby/JRubyRiverModule.java')
end

desc "bundle java lib"
task :libjar do
  system('jar cf river-jruby.jar -C src .')
end

desc "bundle support scripts"
task :scriptjar do
  system('jar cf river-libs.jar lib/*')
end

desc "package the plugin"
task :plugin do
  system("zip river-jruby.zip jruby-complete-1.6.3.jar river-jruby.jar river-libs.jar")
end

task :default => [:build, :libjar, :scriptjar,  :plugin]
