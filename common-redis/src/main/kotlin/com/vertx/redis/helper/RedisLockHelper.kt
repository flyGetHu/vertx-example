package com.vertx.redis.helper

import cn.hutool.log.StaticLog
import com.vertx.common.config.sharedData
import com.vertx.redis.client.redisClient
import io.vertx.kotlin.coroutines.await
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
    suspend fun lock(key: String, expire: Long): Boolean {
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
     * @param key 锁的key
     * @return 是否解锁成功
     */
    suspend fun unlock(key: String): Boolean {
        try {
            val lock = sharedData.getLockWithTimeout("redis.shared.lock.$key", TimeUnit.SECONDS.toMillis(2)).await()
            try {
                val response = redisClient.get(key).await()
                if (response.toString() == key) {
                    RedisHelper.Str.del(key)
                    return true
                }
                return false
            } finally {
                lock.release()
            }
        } catch (e: Exception) {
            StaticLog.error(e, "redis unlock error")
            return false
        }
    }
}