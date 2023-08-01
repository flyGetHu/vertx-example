package com.vertx.task.verticle

import cn.hutool.log.StaticLog
import com.vertx.common.config.VertxLoadConfig
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import java.time.Instant

class MainVerticle : CoroutineVerticle() {

    override suspend fun start() {
        try {
            val timer = Instant.now()
            VertxLoadConfig.init()
            vertx.deployVerticle(TaskVerticle::class.java.name).await()
            StaticLog.info("启动示例TASK项目成功:${Instant.now().toEpochMilli() - timer.toEpochMilli()}ms")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}