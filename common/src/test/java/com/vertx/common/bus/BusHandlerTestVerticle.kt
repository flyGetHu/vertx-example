package com.vertx.common.bus

import cn.hutool.core.thread.ThreadUtil
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.junit.jupiter.api.Test

object BusHandlerTestVerticle : CoroutineVerticle() {
    override suspend fun start() {
        BusHandler.register(MyBusServiceImpl)
        MyBusServiceImpl.call("test").onSuccess {
            println(it)
        }
    }

    @Test
    fun test() {
        val vertx = Vertx.vertx()
        vertx.deployVerticle(BusHandlerTestVerticle)
        ThreadUtil.sleep(1000)
    }
}

