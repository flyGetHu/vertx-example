package com.vertx.verticle

import com.vertx.bus.EventBusDemoImpl
import com.vertx.common.config.LoadConfig
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await

object MainVerticle : CoroutineVerticle() {
    override suspend fun start() {
        // 加载配置
        LoadConfig.loadConfig()
        vertx.deployVerticle(EventBusVerticle).await()
        vertx.setPeriodic(1000) {
            EventBusDemoImpl.call("vertx").onSuccess {
                println(it)
            }.onFailure {
                println(it)
            }
        }
    }
}