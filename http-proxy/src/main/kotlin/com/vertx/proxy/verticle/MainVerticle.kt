package com.vertx.proxy.verticle

import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.proxy.handler.ProxyHandler
import io.vertx.httpproxy.HttpProxy
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await


class MainVerticle : CoroutineVerticle() {
    override suspend fun start() {
        val router = Router.router(vertx)
        val httpClientOptions = HttpClientOptions()
        httpClientOptions.isSsl = true
        httpClientOptions.isTrustAll = true
        val httpClient = vertx.createHttpClient(httpClientOptions)
        val httpProxy = HttpProxy.reverseProxy(httpClient)
            .origin(443, "takeno.bailidaming.com")
        router.get("/server/info")
            .handler(
                ProxyHandler.create(
                    httpProxy
                )
            )
        val proxyServer: HttpServer = vertx.createHttpServer()
        proxyServer.requestHandler(router)
        proxyServer.listen(8080).await()
    }
}