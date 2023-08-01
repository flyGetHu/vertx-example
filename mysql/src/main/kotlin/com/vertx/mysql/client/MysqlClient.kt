/**
 * This file contains the definition of the MysqlClient object, which provides a MySQL client for the application.
 * The object contains a single function, init(), which initializes the MySQL client with the configuration specified in the application configuration file.
 * The MySQL client is created using the io.vertx.mysqlclient.MySQLPool class, which provides a connection pool for MySQL databases.
 * The function initializes the MySQLConnectOptions and PoolOptions objects with the configuration values, and creates a new MySQLPool object using the Vert.x event bus.
 * The function tests the connection by executing a simple query, and sets the mysqlClient variable to the new MySQLPool object.
 * If the connection is successful, the function logs a message to the console.
 */
package com.vertx.mysql.client

import cn.hutool.log.StaticLog
import com.vertx.common.config.appConfig
import com.vertx.common.config.isInit
import com.vertx.common.config.vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.PoolOptions


// 全局mysql客户端
lateinit var mysqlPoolClient: io.vertx.mysqlclient.MySQLPool


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
    suspend fun init(config: com.vertx.common.entity.Mysql) {
        if (!isInit) {
            StaticLog.error("全局初始化未完成,请先调用:VertxLoadConfig.init()")
            throw Exception("全局初始化未完成,请先调用:VertxLoadConfig.init()")
        }
        val mySQLConnectOptions = io.vertx.mysqlclient.MySQLConnectOptions()
        val host = config.host
        if (host.isBlank()) {
            StaticLog.error("mysql host is blank:{}", config)
            throw Exception("mysql host is blank")
        }
        mySQLConnectOptions.host = host
        mySQLConnectOptions.port = config.port
        val username = config.username
        if (username.isBlank()) {
            StaticLog.error("mysql username is blank:{}", config)
            throw Exception("mysql username is blank")
        }
        mySQLConnectOptions.user = username
        val password = config.password
        if (password.isBlank()) {
            StaticLog.error("mysql password is blank:{}", config)
            throw Exception("mysql password is blank")
        }
        mySQLConnectOptions.password = password
        val database = config.database
        if (database.isBlank()) {
            StaticLog.error("mysql database is blank:{}", config)
            throw Exception("mysql database is blank")
        }
        mySQLConnectOptions.database = database
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
        mysqlPoolClient = client
        StaticLog.info("mysql连接成功")
    }
}