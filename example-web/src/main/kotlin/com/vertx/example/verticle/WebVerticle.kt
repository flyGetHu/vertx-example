package com.vertx.example.verticle

import com.vertx.example.web.WebRouter
import com.vertx.webserver.entity.WebServiceOptions
import com.vertx.webserver.helper.VertxWebConfig
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * web启动类
 */
class WebVerticle : CoroutineVerticle() {
    override suspend fun start() {
        // 启动http服务
        val webServiceOptions = WebServiceOptions()
        webServiceOptions.initRouter = WebRouter::init
        VertxWebConfig.startHttpServer(webServiceOptions)
    }
}