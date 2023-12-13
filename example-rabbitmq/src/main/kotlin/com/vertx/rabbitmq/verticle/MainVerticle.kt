package com.vertx.rabbitmq.verticle

import cn.hutool.log.StaticLog
import com.vertx.common.config.VertxLoadConfig
import com.vertx.common.config.appConfig
import com.vertx.rabbitmq.client.RabbitMqClient
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import java.time.Instant

class MainVerticle : CoroutineVerticle() {
    override suspend fun start() {
        try {
            val timer = Instant.now()
            VertxLoadConfig.init()
            // 初始化rabbitmq
            RabbitMqClient.init(appConfig.mq?.rabbitmq!!)
            vertx.deployVerticle(RabbitMqConsumerVerticle::class.java.name).await()
            StaticLog.info("启动示例BUS项目成功:${Instant.now().toEpochMilli() - timer.toEpochMilli()}ms")
        } catch (e: Exception) {
            StaticLog.error(e, "启动示例Rabbitmq项目失败:")
        }
    }
}