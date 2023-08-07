package com.vertx.redis.helper

import cn.hutool.log.StaticLog
import com.vertx.common.config.sharedData
import com.vertx.common.config.vertx
import com.vertx.redis.client.redisClient
import com.vertx.redis.enums.RedisLockKeyEnum
import io.vertx.core.Promise
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * redis分布式锁
 * 在vertx集群环境里可以使用自带的sharedData.getLockWithTimeout 来实现分布式锁
 * 在需要持久化的场景下，可以使用redis来实现分布式锁
 * 以及在和springboot交互的情况下，可以使用redis来实现分布式锁
 * redis分布式锁的特点：
 * 1. 互斥性。在任意时刻，只有一个客户端能持有锁。
 * 2. 不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续其他客户端能加锁。
 * 3. 具有容错性。只要大部分的Redis节点正常运行，客户端就可以加锁和解锁。
 * 4. 解铃还须系铃人。加锁和解锁必须是同一个客户端。
 */
object RedisLockHelper {

    /**
     * 加锁, 如果锁已经存在，返回false
     * 如果锁不存在，返回true并且设置锁的过期时间
     * @param key 锁的key
     * @param expire 锁的过期时间
     * @return 是否获得锁
     */
    suspend fun lock(redisLockKeyEnum: RedisLockKeyEnum, expire: Long): Boolean {
        val key = redisLockKeyEnum.name.lowercase()
        val response = redisClient.setnx(key, key).await()
        if (response.toInteger() == 1) {
            // 设置成功，获得锁
            RedisHelper.Str.expire(key, expire)
            return true
        }
        return false
    }

    /**
     * 解锁
     * 如果锁存在，且锁的值和传入的值相等，删除锁并返回true
     * 如果锁不存在，或者锁的值和传入的值不相等，返回false
     * @param redisLockKeyEnum 锁的key枚举 用于区分不同的锁
     * @return 是否解锁成功
     */
    suspend fun unlock(redisLockKeyEnum: RedisLockKeyEnum): Boolean {
        //保证原子性
        val promise = Promise.promise<Boolean>()
        val key = redisLockKeyEnum.name.lowercase()
        val redisLock = "redis.shard.lock.$key"
        sharedData.getLockWithTimeout(redisLock, TimeUnit.SECONDS.toMillis(3)).onComplete {
            CoroutineScope(vertx.dispatcher()).launch {
                if (it.succeeded()) {
                    try {
                        val response = RedisHelper.Str.get(key)
                        if (!response.isNullOrBlank() && response == key) {
                            RedisHelper.Str.del(key)
                            promise.complete(true)
                        } else {
                            promise.complete(false)
                        }
                    } finally {
                        it.result().release()
                        StaticLog.info("redis分布式锁解锁成功")
                    }
                } else {
                    StaticLog.error(it.cause(), "redis分布式锁解锁失败")
                    promise.complete(false)
                }
            }
        }
        return promise.future().await()
    }
}