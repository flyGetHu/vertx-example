package com.vertx.redis.client

import cn.hutool.log.StaticLog
import com.vertx.common.config.isInit
import com.vertx.common.config.vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.redis.client.Redis
import io.vertx.redis.client.RedisAPI
import io.vertx.redis.client.RedisOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * redis客户端
 */
lateinit var redisClient: RedisAPI

/**
 * 将创建一个redis客户端并在存在以下情况时设置一个重新连接处理程序 连接中的异常。
 * @param config Redis配置 [com.vertx.common.entity.app.Redis]
 */
object RedisClient {
    /**
     * 初始化
     * @param config Redis配置 [com.vertx.common.entity.app.Redis]
     */
    suspend fun init(config: com.vertx.common.entity.app.Redis?) {
        if (!isInit) {
            throw Exception("vertx is not init")
        }
        if (config == null) {
            throw Exception("redis config is null")
        }
        createRedisClient(config)
        StaticLog.info("redisClient 初始化成功")
    }

    private suspend fun createRedisClient(config: com.vertx.common.entity.app.Redis) {
        val redisOptions = RedisOptions()
        val host = config.host
        if (host.isBlank()) {
            throw Exception("redis host is blank")
        }
        val port = config.port
        val database = config.database
        val password = config.password
        val url = "redis://$host:$port/$database"
        redisOptions.addConnectionString(url)
        redisOptions.setPassword(password)
        redisOptions.maxPoolSize = config.maxPoolSize
        redisOptions.maxPoolWaiting = config.maxPoolWaiting
        val connection = Redis.createClient(vertx, redisOptions).connect().await()
        connection.exceptionHandler {
            StaticLog.error(it, "redisClient链接断开")
            CoroutineScope(vertx.dispatcher()).launch {
                attemptReconnect(0, config)
            }
        }
        redisClient = RedisAPI.api(connection)
    }


    /**
     * Attempt to reconnect up to MAX_RECONNECT_RETRIES
     */
    private suspend fun attemptReconnect(retry: Int, config: com.vertx.common.entity.app.Redis) {
        if (retry > 60) {
            StaticLog.error("redisClient链接断开,重试次数超过60次，不再重试")
        } else {
            // retry with backoff up to 10240 ms
            delay(1000)
            try {
                createRedisClient(config)
            } catch (e: Throwable) {
                StaticLog.error(e, "redisClient链接断开,重试第${retry + 1}次")
                attemptReconnect(retry + 1, config)
            }
        }
    }
}