package com.vertx.webclient.client

import com.vertx.common.config.vertx
import com.vertx.common.entity.app.WebClient

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
        config: WebClient?
    ) {
        if (config == null) {
            throw Exception("webClient配置为空")
        }
        val webClientOptions = io.vertx.ext.web.client.WebClientOptions()

        // 设置是否保持连接
        webClientOptions.setKeepAlive(true)

        // 设置连接超时时间
        webClientOptions.setConnectTimeout(config.connectTimeout)

        // 设置读取空闲超时时间
        webClientOptions.setReadIdleTimeout(config.readIdleTimeout)

        // 设置写入空闲超时时间
        webClientOptions.setWriteIdleTimeout(config.writeIdleTimeout)

        // 设置空闲超时时间
        webClientOptions.idleTimeout = config.idleTimeout

        // 设置是否信任所有证书
        webClientOptions.setTrustAll(true)

        // 设置最大连接池大小
        webClientOptions.setMaxPoolSize(config.maxPoolSize)

        // 设置用户代理
        webClientOptions.setUserAgent("vertx-web-client")

        // 创建WebClient实例
        webClient = io.vertx.ext.web.client.WebClient.create(vertx, webClientOptions)
    }
}