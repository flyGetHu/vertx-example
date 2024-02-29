@echo off
cd vertx-common-core-kt
echo Executing Maven command in vertx-common-core-kt
mvn clean install -U -fae -T 1C org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests
cd ..

for /D %%i in (vertx-common*) do (
    if not "%%i"=="vertx-common-core-kt" (
        cd %%i
        echo Executing Maven command in %%i
        mvn clean install -U -fae -T 1C org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests
        cd ..
    )
)