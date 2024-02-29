@echo off
for /D %%i in (vertx-common*) do (
    cd %%i
    echo Executing Maven command in %%i
    mvn clean install -U -fae -T 1C org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests
    cd ..
)