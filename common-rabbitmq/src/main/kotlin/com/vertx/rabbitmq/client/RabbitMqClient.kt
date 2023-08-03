/**
 * This file contains the RabbitMqClient object which is used to create and initialize a RabbitMQ client.
 * The RabbitMqClient object has a single public function called init which takes a Rabbitmq object as a parameter.
 * The init function initializes the RabbitMQ client with the provided configuration.
 * The RabbitMqClient object also contains a lateinit variable called rabbitMqClient which is used to store the created RabbitMQ client.
 * The RabbitMqClient object is responsible for setting up the RabbitMQ client with the provided configuration and starting it.
 * The RabbitMqClient object also sets the client to confirm mode and sets the maximum QoS for the client.
 */
package com.vertx.rabbitmq.client

import com.vertx.common.config.vertx
import com.vertx.common.entity.app.Rabbitmq
import io.vertx.kotlin.coroutines.await
import io.vertx.rabbitmq.RabbitMQOptions


//rabbitmq客户端
lateinit var rabbitMqClient: io.vertx.rabbitmq.RabbitMQClient

/**
 * rabbitmq客户端
 */
object RabbitMqClient {

    /**
     * rabbitmq客户端初始化
     */
    suspend fun init(config: Rabbitmq?) {
        if (config == null) {
            throw Exception("rabbitmq config is null")
        }
        val rabbitMQOptions = RabbitMQOptions()
        val host = config.host
        if (host.isBlank()) {
            throw Exception("rabbitmq host is blank")
        }
        rabbitMQOptions.host = host
        rabbitMQOptions.port = config.port
        val username = config.username
        if (username.isBlank()) {
            throw Exception("rabbitmq username is blank")
        }
        rabbitMQOptions.user = username
        val password = config.password
        if (password.isBlank()) {
            throw Exception("rabbitmq password is blank")
        }
        rabbitMQOptions.password = password
        //连接超时时间
        rabbitMQOptions.connectionTimeout = config.connectionTimeout
        //心跳超时时间
        rabbitMQOptions.handshakeTimeout = config.handshakeTimeout
        //虚拟主机
        rabbitMQOptions.virtualHost = config.virtualHost
        //自动重连
        rabbitMQOptions.isAutomaticRecoveryEnabled = config.automaticRecoveryEnabled
        //重连间隔时间
        rabbitMQOptions.networkRecoveryInterval = config.networkRecoveryInterval
        //重连最大次数
        rabbitMQOptions.reconnectAttempts = config.reconnectAttempts
        //重连间隔时间
        rabbitMQOptions.reconnectInterval = config.reconnectInterval
        //请求通道最大数
        rabbitMQOptions.requestedChannelMax = config.requestedChannelMax
        //请求心跳超时时间
        rabbitMQOptions.requestedHeartbeat = config.requestedHeartbeat

        //创建rabbitmq客户端
        rabbitMqClient = io.vertx.rabbitmq.RabbitMQClient.create(vertx, rabbitMQOptions)
        //启动rabbitmq客户端
        rabbitMqClient.start().await()
        //设置客户端为confirm模式
        rabbitMqClient.confirmSelect().await()
        //设置客户端最大qos
        rabbitMqClient.basicQos(config.maxQos).await()
    }
}