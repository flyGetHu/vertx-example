package com.vertx.example.service

import com.vertx.common.model.User
import com.vertx.eventbus.bus.DemoBusHandler
import io.vertx.kotlin.coroutines.await

/**
 * 事件总线服务调用方
 * @constructor 创建一个事件总线服务调用方
 */
object TestBusService {

    /**
     * 测试事件总线
     * @return AppConfig
     */
    suspend fun testBus(): User {
        return DemoBusHandler().call("test").await()
    }
}