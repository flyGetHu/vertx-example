@echo off
chcp 65001
setlocal enabledelayedexpansion

mvn clean install -U package -DskipTests

mvn clean install -U org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests
endlocal
