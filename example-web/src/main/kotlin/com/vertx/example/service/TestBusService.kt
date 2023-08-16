package com.vertx.example.service

import cn.hutool.log.StaticLog
import com.vertx.common.config.sharedData
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
    suspend fun testBus(): List<User> {
        val counterNum = sharedData.getCounter("counter").await().addAndGet(1).await()
        StaticLog.info("TestBusService: $counterNum")
        return DemoBusHandler().call("test").await()
    }
}