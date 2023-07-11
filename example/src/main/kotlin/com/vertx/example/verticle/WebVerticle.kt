package com.vertx.example.verticle

import com.vertx.common.config.appConfig
import com.vertx.common.config.startHttpServer
import com.vertx.example.web.WebRouter
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * web启动类
 */
class WebVerticle : CoroutineVerticle() {
    override suspend fun start() {
        // 启动http服务
        startHttpServer(vertx, WebRouter::init, appConfig.webServer)
    }
}