package com.vertx.rabbitmq.handler

import cn.hutool.log.StaticLog
import com.vertx.common.entity.mq.MqMessageData
import com.vertx.common.model.User

/**
 * rabbit队列处理器接口实现
 */
object TestCustomerHandlerImpl : TestCustomerHandler() {

    override var handler: suspend (User) -> String? = { message: User ->
        StaticLog.info("消费成功:${message}")
        null
    }

    override var persistence: suspend (MqMessageData<User>) -> String? = { message: MqMessageData<User> ->
        StaticLog.info("持久化成功:${message}")
        null
    }

    override var callback: suspend (String, String) -> Unit = { msg, msgId ->
        StaticLog.info("回调成功:${msg},msgId:${msgId}")
    }
}