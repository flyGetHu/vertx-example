package com.vertx.common.client

// WEB客户端
lateinit var webClient: io.vertx.ext.web.client.WebClient

/**
 * WEB客户端
 */
object WebClient {
    /**
     * 初始化webClient
     * @param maxPollSize 最大连接数 默认16
     * @param connectTimeout 连接超时时间 默认10秒
     */
    fun init(maxPollSize: Int = 16, connectTimeout: Int = 10000) {
        val webClientOptions = io.vertx.ext.web.client.WebClientOptions()
        webClientOptions.setKeepAlive(true)
        webClientOptions.setConnectTimeout(connectTimeout)
        webClientOptions.setTrustAll(true)
        webClientOptions.setMaxPoolSize(maxPollSize)
        webClientOptions.setUserAgent("vertx-web-client")
        webClient = io.vertx.ext.web.client.WebClient.create(io.vertx.core.Vertx.vertx(), webClientOptions)
    }
}