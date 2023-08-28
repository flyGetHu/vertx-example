package com.vertx.rabbitmq.verticle

import com.vertx.rabbitmq.handler.TestCustomerHandlerImpl
import com.vertx.rabbitmq.helper.RabbitMqHelper
import io.vertx.kotlin.coroutines.CoroutineVerticle

class RabbitMqConsumerVerticle : CoroutineVerticle() {
    override suspend fun start() {
        RabbitMqHelper.registerConsumer(TestCustomerHandlerImpl)
    }
}