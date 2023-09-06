package com.vertx.example.verticle

import cn.hutool.log.StaticLog
import com.vertx.common.config.VertxLoadConfig
import com.vertx.common.config.appConfig
import com.vertx.mysql.client.MysqlClient
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import java.time.Instant

class MainVerticle : CoroutineVerticle() {

    override suspend fun start() {
        try {
            val timer = Instant.now()
            VertxLoadConfig.init()
            // 初始化mysql
            MysqlClient.init(appConfig.database?.mysql!!)
            vertx.deployVerticle(BusVerticle::class.java.name).await()
            StaticLog.info("启动示例BUS项目成功:${Instant.now().toEpochMilli() - timer.toEpochMilli()}ms")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}