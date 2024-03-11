package com.vertx.rabbitmq.handler

import cn.hutool.log.StaticLog
import com.vertx.common.annotations.UniqueAddress
import com.vertx.common.entity.mq.MqMessageData
import com.vertx.common.enums.ModelEnum
import com.vertx.common.model.User
import com.vertx.rabbitmq.enums.RabbitMqExChangeEnum

@UniqueAddress("dev.test.test.2023-08-03.test.fanout")
open class TestCustomerHandler : RabbitMqHandler<User> {
    override val requestClass: Class<User> = User::class.java
    override val exchange: RabbitMqExChangeEnum = RabbitMqExChangeEnum.TESTRabbitMqExChangeEnum
    override val moduleName: ModelEnum = ModelEnum.TEST__MODEL

    override val queueName: String = "test"
    override var date: String = "2023-08-03"
    override var durable: Boolean = true
    override var exclusive: Boolean = false
    override var maxInternalQueueSize: Int = 100
    override var autoAck: Boolean = false
    override var maxRetry: Int = 3
    override var retryInterval: Long = 1000
    override suspend fun callback(msg: String, msgId: String) {
        StaticLog.info("回调成功:${msg},msgId:${msgId}")
    }

    override suspend fun persistence(message: MqMessageData<User>): String? {
        StaticLog.info("持久化函数执行成功")
        return null
    }

    override suspend fun handler(message: User): String? {
        if (message.name == "test") {
            StaticLog.info("处理函数执行成功")
            return "处理函数执行成功"
        }
        StaticLog.info("处理函数执行成功")
        return null
    }
}