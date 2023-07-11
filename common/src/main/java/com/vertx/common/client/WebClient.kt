package com.vertx.common.client

// WEB客户端
lateinit var webClient: io.vertx.ext.web.client.WebClient

/**
 * WEB客户端
 */
object WebClient {
    /**
     * 初始化webClient
     * @param config 配置
     */
    fun init(
        config: com.vertx.common.entity.WebClient = com.vertx.common.entity.WebClient(
            maxPoolSize = 16,
            connectTimeout = 10000,
            readIdleTimeout = 10000,
            writeIdleTimeout = 10000,
            idleTimeout = 10000
        )
    ) {
        val webClientOptions = io.vertx.ext.web.client.WebClientOptions()
        webClientOptions.setKeepAlive(true)
        webClientOptions.setConnectTimeout(config.connectTimeout)
        webClientOptions.setReadIdleTimeout(config.readIdleTimeout)
        webClientOptions.setWriteIdleTimeout(config.writeIdleTimeout)
        webClientOptions.idleTimeout = config.idleTimeout
        webClientOptions.setTrustAll(true)
        webClientOptions.setMaxPoolSize(config.maxPoolSize)
        webClientOptions.setUserAgent("vertx-web-client")
        webClient = io.vertx.ext.web.client.WebClient.create(io.vertx.core.Vertx.vertx(), webClientOptions)
    }
}