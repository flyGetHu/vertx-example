package com.vertx.example.verticle

import cn.hutool.log.StaticLog
import com.vertx.common.config.loadConfig
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import java.time.Duration
import java.time.Instant

class MainVerticle : CoroutineVerticle() {
    override suspend fun start() {
        try {
            val timer = Instant.now()
            // 加载配置
            loadConfig(vertx)
            vertx.deployVerticle(EventBusVerticle::class.java.name).await()
            vertx.deployVerticle(TaskVerticle::class.java.name).await()
            StaticLog.info("启动示例项目成功:${Duration.between(timer, Instant.now()).toMillis()}ms")
        } catch (e: Exception) {
            StaticLog.error(e, "启动示例项目失败:")
        }
    }
}