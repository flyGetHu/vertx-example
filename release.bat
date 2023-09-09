@echo off
chcp 65001
setlocal enabledelayedexpansion

rem 设置Maven命令
set MAVEN_COMMAND=mvn clean install org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests

rem 获取当前目录路径
set "CURRENT_DIR=%CD%"

rem 遍历当前目录下的子目录
for /d %%i in (*vertx-common*) do (
    set "SUB_DIR=%%i"
    if exist "!SUB_DIR!" (
        pushd "!SUB_DIR!"
        echo 进入目录: !CD!
        call %MAVEN_COMMAND%
        popd
        echo 切换回目录: !CURRENT_DIR!
    ) else (
        echo 目录不存在: %%i
    )
)

endlocal
