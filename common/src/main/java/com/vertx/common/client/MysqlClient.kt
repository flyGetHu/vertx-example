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
import com.vertx.common.utils.MysqlUtil.buildDeleteSql
import com.vertx.common.utils.MysqlUtil.buildInsertSql
import com.vertx.common.utils.MysqlUtil.buildSelectSql
import com.vertx.common.utils.MysqlUtil.buildUpdateSql
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLClient
import io.vertx.sqlclient.PoolOptions
import org.jooq.Condition


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
        mysqlClient = client
        StaticLog.info("mysql连接成功")
    }

    /**
     * 插入数据
     * @param data 数据对象
     * @return 获取最新的id
     */
    suspend fun insert(data: Any): Long {
        val sql = buildInsertSql(data)
        val rowRowSet = mysqlClient.query(sql).execute().await()
        return rowRowSet.property(MySQLClient.LAST_INSERTED_ID)
    }

    /**
     * 更新数据
     * @param data 数据对象
     * @param where 条件
     * @param isNll 是否更新空值
     * @return 受影响的行数
     */
    suspend fun update(data: Any, where: Condition, isNll: Boolean = false): Int {
        val sql = buildUpdateSql(data, where, isNll)
        val rowRowSet = mysqlClient.query(sql).execute().await()
        return rowRowSet.rowCount()
    }

    /**
     * 删除数据
     * @param clazz 类对象
     * @param where 条件
     * @return 受影响的行数
     */
    suspend fun delete(clazz: Class<*>, where: Condition): Int {
        val sql = buildDeleteSql(clazz, where)
        val rowRowSet = mysqlClient.query(sql).execute().await()
        return rowRowSet.rowCount()
    }

    /**
     * 查询数据
     * @param clazz 类对象
     * @param where 条件
     * @param fields 查询字段
     * @return 查询结果
     */
    suspend fun <T> select(clazz: Class<T>, where: Condition, fields: List<String>): List<T> {
        val sql = buildSelectSql(clazz, where, fields)
        val rowRowSet = mysqlClient.query(sql).execute().await().map {
            it.toJson().mapTo(clazz)
        }
        return rowRowSet
    }
}