package com.vertx.example.verticle

import com.vertx.example.web.WebRouter
import com.vertx.webserver.config.VertxWebConfig
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * web启动类
 */
class WebVerticle : CoroutineVerticle() {
    override suspend fun start() {
        // 启动http服务
        VertxWebConfig.startHttpServer(WebRouter::init)
    }
}