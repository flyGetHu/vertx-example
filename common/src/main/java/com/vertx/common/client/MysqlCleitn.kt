/**
 * This file contains the definition of the MysqlClient object, which provides a MySQL client for the application.
 * The object contains a single function, init(), which initializes the MySQL client with the configuration specified in the application configuration file.
 * The MySQL client is created using the io.vertx.mysqlclient.MySQLPool class, which provides a connection pool for MySQL databases.
 * The function initializes the MySQLConnectOptions and PoolOptions objects with the configuration values, and creates a new MySQLPool object using the Vert.x event bus.
 * The function tests the connection by executing a simple query, and sets the mysqlClient variable to the new MySQLPool object.
 * If the connection is successful, the function logs a message to the console.
 */
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
        // 重试次数
        mySQLConnectOptions.reconnectAttempts = 30
        // 重试间隔
        mySQLConnectOptions.reconnectInterval = 1000
        // 连接池
        val poolOptions = PoolOptions()
        // 连接池名称 用于日志
        poolOptions.name = "mysql-pool-${appConfig.app.name}-${appConfig.app.version}"
        // 最大连接数
        poolOptions.maxSize = config.maxPoolSize
        // 空闲连接超时时间
        poolOptions.idleTimeout = config.idleTimeout
        // 连接超时时间
        poolOptions.connectionTimeout = config.connectionTimeout
        // 最大生命周期
        poolOptions.maxLifetime = config.maxLifetime
        // 最大等待队列大小
        poolOptions.maxWaitQueueSize = config.maxWaitQueueSize
        val client = io.vertx.mysqlclient.MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions)
        //测试连接
        client.query("select 1").execute().await()
        mysqlClient = client
        StaticLog.info("mysql连接成功")
    }
}