# it might be used Spring boot maven plugin (or gradle in other projects)
# but it's nicer to use the most generic approach despite the build tool
sudo java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n  -jar ./target/fs-assistant-0.0.1-SNAPSHOT.jar 
