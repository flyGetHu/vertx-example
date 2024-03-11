package com.vertx.rabbitmq.handler

import com.vertx.common.entity.mq.MqMessageData
import com.vertx.common.model.User

/**
 * rabbit队列处理器接口实现
 */
object TestCustomerHandlerImpl : TestCustomerHandler() {

    override suspend fun handler(message: User): String? {
        return super.handler(message)
    }

    override suspend fun persistence(message: MqMessageData<User>): String? {
        return super.persistence(message)
    }

    override suspend fun callback(msg: String, msgId: String) {
        super.callback(msg, msgId)
    }
}