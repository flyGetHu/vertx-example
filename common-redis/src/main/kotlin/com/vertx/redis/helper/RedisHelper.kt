package com.vertx.redis.helper

import cn.hutool.log.StaticLog
import com.vertx.redis.client.redisClient
import io.vertx.kotlin.coroutines.await

/**
 * redis帮助类
 * 封装redis的常用操作
 * 1. string 类型的操作
 * 2. hash 类型的操作
 * 3. list 类型的操作
 * 4. set 类型的操作
 * 5. zset 类型的操作
 * 6. geo 类型的操作
 * 7. transaction 类型的操作
 */
object RedisHelper {

    /**
     * string 类型的操作
     */
    object Str {

        /**
         * 设置key的值为value
         * @param key
         * @param value
         * @return
         */
        suspend fun set(key: String, value: String): Boolean {
            val res = try {
                redisClient.set(listOf(key, value)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis set error")
                false
            }
            return res
        }

        /**
         * 设置key的值为value，并设置过期时间
         * @param key
         * @param value
         * @param seconds
         * @return
         */
        suspend fun set(key: String, value: String, seconds: Long): Boolean {
            val res = try {
                redisClient.setex(key, seconds.toString(), value).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis set expired error")
                false
            }
            return res
        }

        /**
         * 获取key的值
         * @param key
         * @return
         */
        suspend fun get(key: String): String? {
            val res = try {
                redisClient.get(key).await().toString()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis get error")
                null
            }
            return res
        }

        /**
         * 删除key
         * @param key
         * @return
         */
        suspend fun del(key: String): Boolean {
            val res = try {
                redisClient.del(listOf(key)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis del error")
                false
            }
            return res
        }

        /**
         * 设置key的过期时间
         * @param key
         * @param seconds
         * @return
         */
        suspend fun expire(key: String, seconds: Long): Boolean {
            val res = try {
                redisClient.expire(listOf(key, seconds.toString())).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis expire error")
                false
            }
            return res
        }
    }

    /**
     * hash 类型的操作
     */
    object hash {

        /**
         * 设置key的field的值为value
         * @param key
         * @param field
         * @param value
         * @return 成功返回true，失败返回false
         */
        suspend fun hset(key: String, field: String, value: String): Boolean {
            val res = try {
                redisClient.hset(listOf(key, field, value)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis hset error")
                false
            }
            return res
        }

        /**
         * 获取key的field的值
         * @param key
         * @param field
         * @return 成功返回value，失败返回null
         */
        suspend fun hget(key: String, field: String): String? {
            val res = try {
                redisClient.hget(key, field).await().toString()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis hget error")
                null
            }
            return res
        }

        /**
         * 删除key的field
         * @param key
         * @param field
         * @return 成功返回true，失败返回false
         */
        suspend fun hdel(key: String, field: String): Boolean {
            val res = try {
                redisClient.hdel(listOf(key, field)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis hdel error")
                false
            }
            return res
        }

        /**
         * 获取key的所有field和value
         * @param key
         * @return 成功返回map，失败返回null
         */
        suspend fun hgetall(key: String): Map<String, String>? {
            val res = try {
                redisClient.hgetall(key).await().map { it.toString() }.chunked(2).associate { it[0] to it[1] }
            } catch (e: Throwable) {
                StaticLog.error(e, "redis hgetall error")
                null
            }
            return res
        }

        /**
         * 获取key的所有field
         * @param key
         * @return 成功返回list，失败返回null
         */
        suspend fun hkeys(key: String): List<String>? {
            val res = try {
                redisClient.hkeys(key).await().map { it.toString() }
            } catch (e: Throwable) {
                StaticLog.error(e, "redis hkeys error")
                null
            }
            return res
        }

        /**
         * 获取key的所有value
         * @param key
         * @return 成功返回list，失败返回null
         */
        suspend fun hvals(key: String): List<String>? {
            val res = try {
                redisClient.hvals(key).await().map { it.toString() }
            } catch (e: Throwable) {
                StaticLog.error(e, "redis hvals error")
                null
            }
            return res
        }

        /**
         * 获取key的field的数量
         * @param key
         * @return 成功返回数量，失败返回null
         */
        suspend fun hlen(key: String): Long? {
            val res = try {
                redisClient.hlen(key).await().toString().toLong()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis hlen error")
                null
            }
            return res
        }

        /**
         * 判断key的field是否存在
         * @param key
         * @param field
         * @return 成功返回true，失败返回false
         */
        suspend fun hexists(key: String, field: String): Boolean {
            val res = try {
                redisClient.hexists(key, field).await().toString().toBoolean()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis hexists error")
                false
            }
            return res
        }
    }

    /**
     * list 类型的操作
     */
    object list {

        /**
         * 从左边插入数据
         * @param key
         * @param value
         * @return 成功返回true，失败返回false
         */
        suspend fun lpush(key: String, value: String): Boolean {
            val res = try {
                redisClient.lpush(listOf(key, value)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis lpush error")
                false
            }
            return res
        }

        /**
         * 从右边插入数据
         * @param key
         * @param value
         * @return 成功返回true，失败返回false
         */
        suspend fun rpush(key: String, value: String): Boolean {
            val res = try {
                redisClient.rpush(listOf(key, value)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis rpush error")
                false
            }
            return res
        }

        /**
         * 从左边弹出数据
         * @param key
         * @param count 弹出的数量
         * @return 成功返回value，失败返回null
         */
        suspend fun lpop(key: String, count: Int = 1): String? {
            if (count < 1) return null
            val res = try {
                redisClient.lpop(listOf(key, count.toString())).await().toString()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis lpop error")
                null
            }
            return res
        }

        /**
         * 从右边弹出数据
         * @param key
         * @param count 弹出的数量
         * @return 成功返回value，失败返回null
         */
        suspend fun rpop(key: String, count: Int = 1): String? {
            if (count < 1) return null
            val res = try {
                redisClient.rpop(listOf(key, count.toString())).await().toString()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis rpop error")
                null
            }
            return res
        }

        /**
         * 获取list的长度
         * @param key
         * @return 成功返回长度，失败返回null
         */
        suspend fun llen(key: String): Long? {
            val res = try {
                redisClient.llen(key).await().toString().toLong()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis llen error")
                null
            }
            return res
        }

        /**
         * 获取list的指定范围的数据
         * @param key
         * @param start 开始位置
         * @param stop 结束位置
         * @return 成功返回list，失败返回null
         */
        suspend fun lrange(key: String, start: Int, stop: Int): List<String>? {
            if (start < 0 || stop < 0) return null
            val res = try {
                redisClient.lrange(key, start.toString(), stop.toString()).await().map { it.toString() }
            } catch (e: Throwable) {
                StaticLog.error(e, "redis lrange error")
                null
            }
            return res
        }

        /**
         * 获取list的指定位置的数据
         * @param key
         * @param index 位置
         * @return 成功返回value，失败返回null
         */
        suspend fun lindex(key: String, index: Int): String? {
            if (index < 0) return null
            val res = try {
                redisClient.lindex(key, index.toString()).await().toString()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis lindex error")
                null
            }
            return res
        }

        /**
         * 删除list的指定位置的数据
         * @param key
         * @param start 开始位置
         * @param stop 结束位置
         * @return 成功返回true，失败返回false
         */
        suspend fun lrem(key: String, start: Int, stop: Int): Boolean {
            if (start > stop) return false
            val res = try {
                redisClient.lrem(key, start.toString(), stop.toString()).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis lrem error")
                false
            }
            return res
        }
    }

    /**
     * set 类型的操作
     */

    object set {

        /**
         * 添加数据
         * @param key
         * @param value
         * @return 成功返回true，失败返回false
         */
        suspend fun sadd(key: String, value: String): Boolean {
            val res = try {
                redisClient.sadd(listOf(key, value)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis sadd error")
                false
            }
            return res
        }

        /**
         * 删除数据
         * @param key
         * @param value
         * @return 成功返回true，失败返回false
         */
        suspend fun srem(key: String, value: String): Boolean {
            val res = try {
                redisClient.srem(listOf(key, value)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis srem error")
                false
            }
            return res
        }

        /**
         * 获取set的长度
         * @param key
         * @return 成功返回长度，失败返回null
         */
        suspend fun scard(key: String): Long? {
            val res = try {
                redisClient.scard(key).await().toString().toLong()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis scard error")
                null
            }
            return res
        }

        /**
         * 判断是否存在
         * @param key
         * @param value
         * @return 成功返回true，失败返回false
         */
        suspend fun sismember(key: String, value: String): Boolean {
            val res = try {
                redisClient.sismember(key, value).await().toInteger() == 1
            } catch (e: Throwable) {
                StaticLog.error(e, "redis sismember error")
                false
            }
            return res
        }

        /**
         * 获取set的所有数据
         * @param key
         * @return 成功返回list，失败返回null
         */
        suspend fun smembers(key: String): List<String>? {
            val res = try {
                redisClient.smembers(key).await().map { it.toString() }
            } catch (e: Throwable) {
                StaticLog.error(e, "redis smembers error")
                null
            }
            return res
        }

        /**
         * 随机获取set的数据
         * @param key
         * @param count 获取的数量
         * @return 成功返回list，失败返回null
         */
        suspend fun srandmember(key: String, count: Int = 1): List<String>? {
            if (count < 1) return null
            val res = try {
                redisClient.srandmember(listOf(key, count.toString())).await().map { it.toString() }
            } catch (e: Throwable) {
                StaticLog.error(e, "redis srandmember error")
                null
            }
            return res
        }
    }

    /**
     * zset 类型的操作
     * 有序集合
     */
    object zset {

        /**
         * 添加数据
         * @param key
         * @param value
         * @param score 分数
         * @return 成功返回true，失败返回false
         */
        suspend fun zadd(key: String, value: String, score: Double): Boolean {
            val res = try {
                redisClient.zadd(listOf(key, score.toString(), value)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis zadd error")
                false
            }
            return res
        }

        /**
         * 删除数据
         * @param key
         * @param value
         * @return 成功返回true，失败返回false
         */
        suspend fun zrem(key: String, value: String): Boolean {
            val res = try {
                redisClient.zrem(listOf(key, value)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis zrem error")
                false
            }
            return res
        }

        /**
         * 获取zset的长度
         * @param key
         * @return 成功返回长度，失败返回null
         */
        suspend fun zcard(key: String): Long? {
            val res = try {
                redisClient.zcard(key).await().toString().toLong()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis zcard error")
                null
            }
            return res
        }

        /**
         * 获取指定分数范围的数据
         * @param key
         * @param min 最小分数
         * @param max 最大分数
         * @return 成功返回list，失败返回null
         */
        suspend fun zrangebyscore(key: String, min: Double, max: Double): List<String>? {
            val res = try {
                redisClient.zrangebyscore(listOf(key, min.toString(), max.toString())).await().map { it.toString() }
            } catch (e: Throwable) {
                StaticLog.error(e, "redis zrangebyscore error")
                null
            }
            return res
        }

    }

    /**
     * geo 类型的操作
     */
    object geo {
        /**
         * 添加数据
         * @param key
         * @param longitude 经度
         * @param latitude 纬度
         * @param member 成员
         * @return 成功返回true，失败返回false
         */
        suspend fun geoadd(key: String, longitude: Double, latitude: Double, member: String): Boolean {
            val res = try {
                redisClient.geoadd(listOf(key, longitude.toString(), latitude.toString(), member)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis geoadd error")
                false
            }
            return res
        }

        /**
         * 获取两个成员之间的距离
         * @param key
         * @param member1 成员1
         * @param member2 成员2
         * @param unit 单位
         * @return 成功返回距离，失败返回null
         */
        suspend fun geodist(key: String, member1: String, member2: String, unit: String = "m"): Double? {
            val res = try {
                redisClient.geodist(listOf(key, member1, member2, unit)).await().toString().toDouble()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis geodist error")
                null
            }
            return res
        }

        /**
         * 获取指定成员的经纬度
         * @param key
         * @param member 成员
         * @return 成功返回经纬度，失败返回null
         */
        suspend fun geopos(key: String, member: String): List<String>? {
            val res = try {
                redisClient.geopos(listOf(key, member)).await().map { it.toString() }
            } catch (e: Throwable) {
                StaticLog.error(e, "redis geopos error")
                null
            }
            return res
        }

        /**
         * 获取指定成员的geohash
         * @param key
         * @param member 成员
         * @return 成功返回geohash，失败返回null
         */
        suspend fun geohash(key: String, member: String): String? {
            val res = try {
                redisClient.geohash(listOf(key, member)).await().toString()
            } catch (e: Throwable) {
                StaticLog.error(e, "redis geohash error")
                null
            }
            return res
        }

        /**
         * 获取指定经纬度范围内的成员
         * @param key
         * @param longitude 经度
         * @param latitude 纬度
         * @param radius 范围
         * @param unit 单位
         * @return 成功返回list，失败返回null
         */
        suspend fun georadius(
            key: String, longitude: Double, latitude: Double, radius: Double, unit: String = "m"
        ): List<String>? {
            val res = try {
                redisClient.georadius(listOf(key, longitude.toString(), latitude.toString(), radius.toString(), unit))
                    .await().map { it.toString() }
            } catch (e: Throwable) {
                StaticLog.error(e, "redis georadius error")
                null
            }
            return res
        }

        /**
         * 获取指定成员范围内的成员
         * @param key
         * @param member 成员
         * @param radius 范围
         * @param unit 单位
         * @return 成功返回list，失败返回null
         */
        suspend fun georadiusbymember(key: String, member: String, radius: Double, unit: String = "m"): List<String>? {
            val res = try {
                redisClient.georadiusbymember(listOf(key, member, radius.toString(), unit)).await()
                    .map { it.toString() }
            } catch (e: Throwable) {
                StaticLog.error(e, "redis georadiusbymember error")
                null
            }
            return res
        }
    }

    /**
     * transaction
     * 事务
     * 1.在事务开始时，使用 MULTI 命令来开启一个事务
     * MULTI
     * 2.将多个命令入队到事务中，这些命令不会立即被执行,如果在 MULTI 执行之后，命令出现了错误，那么在 EXEC 执行事务时，所有命令都不会被执行
     * SET key1 "Hello"
     * SET key2 "World"
     * 3.执行 EXEC 命令，将先前入队的所有命令执行,如果在 EXEC 执行之前，命令出现了错误，那么在 EXEC 执行事务时，所有命令都不会被执行
     * EXEC
     * 4.如果要取消事务，可以使用 DISCARD 命令
     * DISCARD
     * 5.在事务执行过程中，可以使用 WATCH 命令来监视任意数量的键值对，一旦其中有一个键被修改（或删除），之后的事务就不会执行
     * WATCH key1 key2
     * 6.如果事务执行之前，有其他客户端修改了其中一个被监视的键，那么事务执行时就会失败，这时客户端可以选择重试事务
     * WATCH key1 key2
     */
    object tran {
        /**
         * 开启事务
         * @return 成功返回true，失败返回false
         */
        suspend fun multi(): Boolean {
            val res = try {
                redisClient.multi().await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis multi error")
                false
            }
            return res
        }

        /**
         * 提交事务
         * @return 成功返回true，失败返回false
         */
        suspend fun exec(): Boolean {
            val res = try {
                redisClient.exec().await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis exec error")
                false
            }
            return res
        }

        /**
         * 回滚事务
         * @return 成功返回true，失败返回false
         */
        suspend fun discard(): Boolean {
            val res = try {
                redisClient.discard().await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis discard error")
                false
            }
            return res
        }

        /**
         * 监视key
         * @param key
         * @return 成功返回true，失败返回false
         */
        suspend fun watch(key: String): Boolean {
            val res = try {
                redisClient.watch(listOf(key)).await()
                true
            } catch (e: Throwable) {
                StaticLog.error(e, "redis watch error")
                false
            }
            return res
        }
    }
}