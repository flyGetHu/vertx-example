package com.vertx.common.client

import cn.hutool.log.StaticLog
import com.vertx.common.config.appConfig
import com.vertx.common.config.vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.PoolOptions

// 全局mysql客户端
lateinit var mysqlClient: io.vertx.mysqlclient.MySQLPool


/**
 * mysql客户端
 * @author huan
 * @date 2023-07-11
 */
object MysqlClient {

    /**
     * mysql客户端
     * @param config 配置 详见common\src\main\kotlin\com\vertx\common\entity\AppConfig.kt
     */
    suspend fun init() {
        val mySQLConnectOptions = io.vertx.mysqlclient.MySQLConnectOptions()
        val config = appConfig.database.mysql
        val host = config.host
        if (host.isBlank()) {
            StaticLog.error("mysql host is blank:{}", config)
            throw Exception("mysql host is blank")
        }
        mySQLConnectOptions.host = host
        mySQLConnectOptions.port = config.port
        mySQLConnectOptions.user = config.username
        mySQLConnectOptions.password = config.password
        mySQLConnectOptions.database = config.database
        mySQLConnectOptions.charset = config.charset
        // 时区
        mySQLConnectOptions.properties["serverTimezone"] = config.timezone
        // 重试策略
        mySQLConnectOptions.reconnectAttempts = 30
        mySQLConnectOptions.reconnectInterval = 1000
        val poolOptions = PoolOptions()
        poolOptions.maxSize = config.maxPoolSize
        poolOptions.idleTimeout = config.idleTimeout
        poolOptions.connectionTimeout = config.connectionTimeout
        poolOptions.maxLifetime = config.maxLifetime
        poolOptions.maxWaitQueueSize = config.maxWaitQueueSize
        val client = io.vertx.mysqlclient.MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions)
        //测试连接
        client.query("select 1").execute().await()
        mysqlClient = client
        StaticLog.info("mysql连接成功")
    }
}