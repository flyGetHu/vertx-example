package com.vertx.common.config

import cn.hutool.core.thread.ThreadUtil
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test

class LoadConfigTest {
    @Test
    fun testLoadConfig() {
        val vertx = Vertx.vertx()
        CoroutineScope(vertx.dispatcher()).launch {
            val configParams = LoadConfig.loadConfig()
            println(configParams)
        }
        ThreadUtil.sleep(1000 * 2)
    }
}