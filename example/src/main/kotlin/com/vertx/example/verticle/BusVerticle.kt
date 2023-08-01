package com.vertx.example.verticle

import cn.hutool.log.StaticLog
import com.vertx.eventbus.handler.BusHandler
import com.vertx.example.bus.DemoBusHandlerImpl
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * 事件总线启动类
 */
class BusVerticle : CoroutineVerticle() {

    override suspend fun start() {
        StaticLog.info("EventBusVerticle启动类开始启动")
        // 注册事件总线服务提供方
        BusHandler.register(DemoBusHandlerImpl)
        StaticLog.info("EventBusVerticle启动类启动成功")
    }
}