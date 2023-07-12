package com.vertx.common.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.http.HttpVersion

/**
 * 配置文件
 */
data class AppConfig(
    // app配置
    @JsonProperty("app") val app: App,
    // 服务配置
    @JsonProperty("webServer") val webServer: WebServer,
    // 数据库配置
    @JsonProperty("database") val database: Database,
    // vertx配置
    @JsonProperty("vertx") val vertx: Vertx,
    // WEB客户端
    @JsonProperty("webClient") val webClient: WebClient
)

/**
 * 应用配置
 */
data class App(
    // 应用名称
    @JsonProperty("name") val name: String,
    // 应用版本
    @JsonProperty("version") val version: String,
    // 应用描述
    @JsonProperty("description") val description: String,
)

/**
 * 服务配置
 */
data class WebServer(
    // 服务端口
    @JsonProperty("port") val port: Int,
    // 服务地址
    @JsonProperty("host") val host: String = "0.0.0.0",
    // 服务版本 alpnVersions
    @JsonProperty("alpnVersions") val alpnVersions: List<HttpVersion> = listOf(
        HttpVersion.HTTP_2,
        HttpVersion.HTTP_1_1
    ),
    // 请求前缀
    @JsonProperty("prefix") val prefix: String,
    // 请求超时时间
    @JsonProperty("timeout") val timeout: Int = 30000,
    // 是否开启日志
    @JsonProperty("logEnabled") val logEnabled: Boolean = true,
    // 不进行请求拦截的地址
    @JsonProperty("ignorePaths") val ignorePaths: List<String> = listOf(),
)

/**
 * 数据库配置
 */
data class Database(
    // mysql配置
    @JsonProperty("mysql") val mysql: Mysql,
)

// mysql配置
data class Mysql(
    // 数据库连接地址
    @JsonProperty("host") val host: String,
    // 数据库端口
    @JsonProperty("port") val port: Int,
    // 数据库用户名
    @JsonProperty("username") val username: String,
    // 数据库密码
    @JsonProperty("password") val password: String,
    // 数据库名称
    @JsonProperty("database") val database: String,
    // 数据库编码格式
    @JsonProperty("charset") val charset: String = "utf8mb4",
    // 时区
    @JsonProperty("timezone") val timezone: String = "UTC",
    // 连接池配置
    // 最大连接数
    @JsonProperty("maxPoolSize") val maxPoolSize: Int,
    // 空闲超时时间
    @JsonProperty("idleTimeout") val idleTimeout: Int,
    // 连接超时时间
    @JsonProperty("connectionTimeout") val connectionTimeout: Int,
    // 最大连接池生存时间
    @JsonProperty("maxLifetime") val maxLifetime: Int,
    // 最大等待队列数
    @JsonProperty("maxWaitQueueSize") val maxWaitQueueSize: Int

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
