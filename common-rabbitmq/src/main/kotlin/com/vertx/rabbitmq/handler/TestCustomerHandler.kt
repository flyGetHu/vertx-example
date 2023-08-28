package com.vertx.rabbitmq.handler

import cn.hutool.log.StaticLog
import com.vertx.common.entity.mq.MqMessageData
import com.vertx.common.enums.ModelEnum
import com.vertx.common.model.User
import com.vertx.rabbitmq.enums.RabbitMqExChangeEnum

open class TestCustomerHandler : RabbitMqHandler<User> {
    override val requestClass: Class<User> = User::class.java
    override val exchange: RabbitMqExChangeEnum = RabbitMqExChangeEnum.TESTRabbitMqExChangeEnum
    override val moduleName: ModelEnum = ModelEnum.TEST

    override val queueName: String = "test"
    override var date: String = "2023-08-03"
    override var durable: Boolean = true
    override var exclusive: Boolean = false
    override var maxInternalQueueSize: Int = 100
    override var autoAck: Boolean = true
    override var maxRetry: Int = 3
    override var retryInterval: Long = 1000
    override var handler: suspend (User) -> String? = { message: User ->
        null
    }

    override var persistence: suspend (MqMessageData<User>) -> String? = { message: MqMessageData<User> ->
        StaticLog.info("持久化函数执行成功")
        null
    }

    override var callback: suspend (String, String) -> Unit = { msg, msgId ->
    }
}