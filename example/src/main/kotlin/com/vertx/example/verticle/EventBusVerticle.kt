package com.vertx.example.verticle

import cn.hutool.log.StaticLog
import com.vertx.common.bus.BusHandler
import com.vertx.common.bus.DemoBusHandler
import com.vertx.example.bus.DemoBusHandlerImpl
import io.vertx.core.Promise
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * 事件总线启动类
 */
class EventBusVerticle : CoroutineVerticle() {

    /**
     * 启动
     */
    override fun start(startFuture: Promise<Void>?) {
        StaticLog.info("EventBusVerticle启动类开始启动")
        BusHandler.register(DemoBusHandlerImpl)
        StaticLog.info("EventBusVerticle启动类启动成功")
        test()
        startFuture?.complete()
    }

    /**
     * 测试
     */
    private fun test() {
        vertx.setPeriodic(1000 * 5) {
            DemoBusHandler().call("vertx").onSuccess {
                StaticLog.info("测试eventbus成功:$it")
            }.onFailure {
                StaticLog.error("测试eventbus失败:$it")
            }
        }
    }
}