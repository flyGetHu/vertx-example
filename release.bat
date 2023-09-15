mvn clean install -U package -DskipTests

mvn clean install -U org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests
