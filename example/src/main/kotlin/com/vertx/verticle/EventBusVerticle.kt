package com.vertx.verticle

import com.vertx.bus.EventBusDemoImpl
import com.vertx.common.bus.EventBusHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle

object EventBusVerticle : CoroutineVerticle() {
    override suspend fun start() {
        EventBusHandler.register(EventBusDemoImpl)
    }
}