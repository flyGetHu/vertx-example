package com.vertx.common.entity

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 配置文件
 */
data class AppConfig(
    // 服务配置
    @JsonProperty("server") val server: Server,
    // 数据库配置
    @JsonProperty("database") val database: Database,
    // vertx配置
    @JsonProperty("vertx") val vertx: Vertx,
    // WEB客户端
    @JsonProperty("webclient") val webClient: WebClient
)

/**
 * 服务配置
 */
data class Server(
    // 服务端口
    @JsonProperty("port") val port: Int
)

/**
 * 数据库配置
 */
data class Database(
    // mysql配置
    @JsonProperty("mysql") val mysql: Mysql,
    // redis配置
    @JsonProperty("redis") val redis: Redis
)

// mysql配置
data class Mysql(
    // 数据库连接地址
    @JsonProperty("url") val url: String,
    // 数据库用户名
    @JsonProperty("username") val username: String,
    // 数据库密码
    @JsonProperty("password") val password: String,
    // 数据库名称
    @JsonProperty("database") val database: String
)

// redis配置
data class Redis(
    // redis连接地址
    @JsonProperty("url") val url: String,
    // redis端口
    @JsonProperty("database") val database: Int,
    // redis密码
    @JsonProperty("password") val password: String
)

/**
 * vertx:
 *   main:
 *     verticle: com.vertx.example.verticle.MainVerticle
 *     instances: 1
 *     ha: false
 */
data class Vertx(
    // verticle
    @JsonProperty("verticle") val verticle: String,
    // instances
    @JsonProperty("instances") val instances: Int,
    // ha
    @JsonProperty("ha") val ha: Boolean,
)

/**
 * webclient:
 *   maxPoolSize: 16
 *   connectTimeout: 2000
 *   readIdleTimeout: 20000
 *   idleTimeout: 10000
 *   writeIdleTimeout: 10000
 */
data class WebClient(
    // 最大连接数
    @JsonProperty("maxPoolSize") val maxPoolSize: Int,
    // 连接超时时间
    @JsonProperty("connectTimeout") val connectTimeout: Int,
    // 读取超时时间
    @JsonProperty("readIdleTimeout") val readIdleTimeout: Int,
    // 空闲超时时间
    @JsonProperty("idleTimeout") val idleTimeout: Int,
    // 写入超时时间
    @JsonProperty("writeIdleTimeout") val writeIdleTimeout: Int
)
