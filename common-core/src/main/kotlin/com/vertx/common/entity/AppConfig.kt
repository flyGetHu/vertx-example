/**
 * This file contains the definition of the AppConfig data class and its related data classes.
 *
 * The AppConfig data class represents the configuration of an application and contains the following properties:
 * - app: an instance of the App data class that represents the application configuration.
 * - webServer: an instance of the WebServer data class that represents the server configuration.
 * - database: an instance of the Database data class that represents the database configuration.
 * - vertx: an instance of the Vertx data class that represents the Vert.x configuration.
 * - webClient: an instance of the WebClient data class that represents the web client configuration.
 *
 * The App data class represents the application configuration and contains the following properties:
 * - name: a string that represents the name of the application.
 * - version: a string that represents the version of the application.
 * - description: a string that represents the description of the application.
 *
 * The WebServer data class represents the server configuration and contains the following properties:
 * - port: an integer that represents the port number of the server.
 * - host: a string that represents the host address of the server.
 * - alpnVersions: a list of HttpVersion objects that represents the supported HTTP/2 and HTTP/1.1 versions.
 * - prefix: a string that represents the prefix of the server's requests.
 * - timeout: an integer that represents the timeout of the server's requests.
 * - logEnabled: a boolean that indicates whether the server's requests should be logged.
 * - ignorePaths: a list of strings that represents the paths that should not be intercepted by the server.
 * - compressionSupported: a boolean that indicates whether the server supports gzip compression.
 * - compressionLevel: an integer that represents the compression level of the server.
 *
 * The Database data class represents the database configuration and contains the following properties:
 * - mysql: an instance of the Mysql data class that represents the MySQL configuration.
 *
 * The Mysql data class represents the MySQL configuration and contains the following properties:
 * - host: a string that represents the host address of the MySQL server.
 * - port: an integer that represents the port number of the MySQL server.
 * - username: a string that represents the username of the MySQL server.
 * - password: a string that represents the password of the MySQL server.
 * - database: a string that represents the name of the MySQL database.
 * - charset: a string that represents the character set of the MySQL database.
 * - timezone: a string that represents the timezone of the MySQL database.
 * - maxPoolSize: an integer that represents the maximum number of connections in the connection pool.
 * - idleTimeout: an integer that represents the idle timeout of the connections in the connection pool.
 * - connectionTimeout: an integer that represents the connection timeout of the connections in the connection pool.
 * - maxLifetime: an integer that represents the maximum lifetime of the connections in the connection pool.
 * - maxWaitQueueSize: an integer that represents the maximum size of the wait queue for connections in the connection pool.
 *
 * The Vertx data class represents the Vert.x configuration and contains the following properties:
 * - verticle: a string that represents the fully qualified name of the main Verticle class.
 * - instances: an integer that represents the number of instances of the main Verticle class.
 * - ha: a boolean that indicates whether the main Verticle class should be deployed in high-availability mode.
 *
 * The WebClient data class represents the web client configuration and contains the following properties:
 * - maxPoolSize: an integer that represents the maximum number of connections in the web client connection pool.
 * - connectTimeout: an integer that represents the connection timeout of the web client.
 * - readIdleTimeout: an integer that represents the read idle timeout of the web client.
 * - idleTimeout: an integer that represents the idle timeout of the web client.
 * - writeIdleTimeout: an integer that represents the write idle timeout of the web client.
 */
package com.vertx.common.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.http.HttpVersion
import kotlin.random.Random

/**
 * 配置文件
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class AppConfig {
    // app配置
    @JsonProperty("app")
    val app: App? = null

    // 服务配置
    @JsonProperty("webServer")
    val webServer: WebServer? = null

    // 数据库配置
    @JsonProperty("database")
    val database: Database? = null

    // mq配置
    @JsonProperty("mq")
    val mq: Mq? = null

    // vertx配置
    @JsonProperty("vertx")
    val vertx: Vertx? = null

    // WEB客户端
    @JsonProperty("webClient")
    val webClient: WebClient? = null
}


/**
 * 应用配置
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class App {
    // 应用名称
    @JsonProperty("name")
    val name: String = ""

    // 应用版本
    @JsonProperty("version")
    val version: String = "1.0.0"

    // 应用描述
    @JsonProperty("description")
    val description: String = ""
}

/**
 * 服务配置
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class WebServer {
    // 服务端口
    @JsonProperty("port")
    val port: Int = Random.nextInt(10000)

    // 服务地址
    @JsonProperty("host")
    val host: String = "0.0.0.0"

    // 服务版本 alpnVersions
    @JsonProperty("alpnVersions")
    val alpnVersions: List<HttpVersion> = listOf(
        HttpVersion.HTTP_2, HttpVersion.HTTP_1_1
    )

    // 请求前缀
    @JsonProperty("prefix")
    val prefix: String = "/api"

    // 请求超时时间
    @JsonProperty("timeout")
    val timeout: Int = 30000

    // 是否开启日志
    @JsonProperty("logEnabled")
    val logEnabled: Boolean = true

    // 不进行请求拦截的地址
    @JsonProperty("ignorePaths")
    val ignorePaths: List<String> = listOf()

    // 是否开启gzip压缩
    @JsonProperty("compressionSupported")
    val compressionSupported: Boolean = true

    // 压缩等级
    @JsonProperty("compressionLevel")
    val compressionLevel: Int = 6
}

/**
 * 数据库配置
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Database {
    // mysql配置
    @JsonProperty("mysql")
    val mysql: Mysql? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Mq {
    // rabbitmq配置
    @JsonProperty("rabbitmq")
    val rabbitmq: Rabbitmq? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Rabbitmq {
    // 主机地址
    @JsonProperty("host")
    val host: String = "127.0.0.1"

    // 端口
    @JsonProperty("port")
    val port: Int = 5672

    // 用户名
    @JsonProperty("username")
    val username: String = "guest"

    // 密码
    @JsonProperty("password")
    val password: String = "guest"

    // 虚拟主机
    @JsonProperty("virtualHost")
    val virtualHost: String = "/"

    // requestedChannelMax 最大通道数
    @JsonProperty("requestedChannelMax")
    val requestedChannelMax: Int = 4095

    // 自动重连
    @JsonProperty("automaticRecoveryEnabled")
    val automaticRecoveryEnabled: Boolean = true

    // 重连间隔
    @JsonProperty("networkRecoveryInterval")
    val networkRecoveryInterval: Long = 5000

    // handshakeTimeout 连接握手超时时间
    @JsonProperty("handshakeTimeout")
    val handshakeTimeout: Int = 10000

    // 连接超时时间
    @JsonProperty("connectionTimeout")
    val connectionTimeout: Int = 10000

    // reconnectAttempts 重连次数
    @JsonProperty("reconnectAttempts")
    val reconnectAttempts: Int = 100

    // reconnectInterval 重连间隔
    @JsonProperty("reconnectInterval")
    val reconnectInterval: Long = 500

    // requestedHeartbeat 心跳间隔
    @JsonProperty("requestedHeartbeat")
    val requestedHeartbeat: Int = 10
}

// mysql配置
@JsonIgnoreProperties(ignoreUnknown = true)
class Mysql {
    // 数据库连接地址
    @JsonProperty("host")
    val host: String = "127.0.0.1"

    // 数据库端口
    @JsonProperty("port")
    val port: Int = 3306

    // 数据库用户名
    @JsonProperty("username")
    val username: String = "root"

    // 数据库密码
    @JsonProperty("password")
    val password: String = "root"

    // 数据库名称
    @JsonProperty("database")
    val database: String = "test"

    // 数据库编码格式
    @JsonProperty("charset")
    val charset: String = "utf8mb4"

    // 时区
    @JsonProperty("timezone")
    val timezone: String = "UTC"

    // 连接池配置
    // 最大连接数
    @JsonProperty("maxPoolSize")
    val maxPoolSize: Int = 16

    // 空闲超时时间
    @JsonProperty("idleTimeout")
    val idleTimeout: Int = 10000

    // 连接超时时间
    @JsonProperty("connectionTimeout")
    val connectionTimeout: Int = 2000

    // 最大连接池生存时间
    @JsonProperty("maxLifetime")
    val maxLifetime: Int = 1800000

    // 最大等待队列数
    @JsonProperty("maxWaitQueueSize")
    val maxWaitQueueSize: Int = Int.MAX_VALUE
}


/**
 * vertx:
 *   main:
 *     verticle: com.vertx.example.verticle.MainVerticle
 *     instances: 1
 *     ha: false
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Vertx {
    // verticle 入口类 项目启动加载的第一个verticle,一般情况下为MainVerticle
    @JsonProperty("verticle")
    val verticle: String = "com.vertx.example.verticle.MainVerticle"

    // instances
    @JsonProperty("instances")
    val instances: Int = 1

    // ha
    @JsonProperty("ha")
    val ha: Boolean = false
}

/**
 * webclient:
 *   maxPoolSize: 16
 *   connectTimeout: 2000
 *   readIdleTimeout: 20000
 *   idleTimeout: 10000
 *   writeIdleTimeout: 10000
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class WebClient {
    // 最大连接数
    @JsonProperty("maxPoolSize")
    val maxPoolSize: Int = 16

    // 连接超时时间
    @JsonProperty("connectTimeout")
    val connectTimeout: Int = 2000

    // 读取超时时间
    @JsonProperty("readIdleTimeout")
    val readIdleTimeout: Int = 20000

    // 空闲超时时间
    @JsonProperty("idleTimeout")
    val idleTimeout: Int = 10000

    // 写入超时时间
    @JsonProperty("writeIdleTimeout")
    val writeIdleTimeout: Int = 10000
}
