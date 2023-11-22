package com.vertx.rabbitmq.handler

import com.vertx.common.config.active
import com.vertx.common.utils.underlineName
import com.vertx.rabbitmq.enums.RabbitMqExChangeTypeEnum
import org.junit.jupiter.api.Test

class TestCustomerHandlerTest {
    @Test
    fun test1() {
        val testCustomerHandler = TestCustomerHandler()
        val exchangeType = testCustomerHandler.exchange.type
        var queueName = "$active.${
            testCustomerHandler.moduleName.modelName.lowercase().underlineName()
        }.${testCustomerHandler.queueName}.${exchangeType.name.lowercase()}.${testCustomerHandler.date}"
        if (exchangeType != RabbitMqExChangeTypeEnum.DEFAULT) {
            queueName += ".${testCustomerHandler.exchange.exchanger}"
        }
        println(queueName)
    }
}