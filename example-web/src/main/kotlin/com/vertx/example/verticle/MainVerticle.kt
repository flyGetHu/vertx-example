package com.vertx.example.verticle

import cn.hutool.log.StaticLog
import com.vertx.common.config.VertxLoadConfig
import com.vertx.common.config.appConfig
import com.vertx.mysql.client.MysqlClient
import com.vertx.webclient.client.WebClient
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import java.time.Duration
import java.time.Instant

class MainVerticle : CoroutineVerticle() {
    override suspend fun start() {
        try {
            val timer = Instant.now()
            // 加载配置
            VertxLoadConfig.init()
            // 初始化web客户端
            WebClient.init(appConfig.webClient)
            // 初始化mysql
            MysqlClient.init(appConfig.database.mysql)
            vertx.deployVerticle(WebVerticle::class.java.name).await()
            StaticLog.info("启动示例项目成功:${Duration.between(timer, Instant.now()).toMillis()}ms")
        } catch (e: Exception) {
            StaticLog.error(e, "启动示例项目失败:")
        }
    }
}