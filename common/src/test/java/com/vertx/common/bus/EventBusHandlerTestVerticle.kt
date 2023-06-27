package com.vertx.common.bus

import cn.hutool.core.thread.ThreadUtil
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.junit.jupiter.api.Test

object EventBusHandlerTestVerticle : CoroutineVerticle() {
    override suspend fun start() {
        EventBusHandler.register(MyEventBusServiceImpl)
        MyEventBusServiceImpl.call("test").onSuccess {
            println(it)
        }
    }

    @Test
    fun test() {
        val vertx = Vertx.vertx()
        vertx.deployVerticle(EventBusHandlerTestVerticle)
        ThreadUtil.sleep(1000)
    }
}

