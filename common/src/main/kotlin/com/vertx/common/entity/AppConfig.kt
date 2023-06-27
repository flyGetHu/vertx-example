package com.vertx.common.entity

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 配置文件
 */
data class AppConfig(
        // 服务配置
        @JsonProperty("server") val server: Server,
        // 数据库配置
        @JsonProperty("database") val database: Database)

/**
 * 服务配置
 */
data class Server(
        // 服务端口
        @JsonProperty("port") val port: Int)

/**
 * 数据库配置
 */
data class Database(
        // mysql配置
        @JsonProperty("mysql") val mysql: Mysql,
        // redis配置
        @JsonProperty("redis") val redis: Redis)

// mysql配置
data class Mysql(
        // 数据库连接地址
        @JsonProperty("url") val url: String,
        // 数据库用户名
        @JsonProperty("username") val username: String,
        // 数据库密码
        @JsonProperty("password") val password: String,
        // 数据库名称
        @JsonProperty("database") val database: String)

// redis配置
data class Redis(
        // redis连接地址
        @JsonProperty("url") val url: String,
        // redis端口
        @JsonProperty("database") val database: Int,
        // redis密码
        @JsonProperty("password") val password: String)
