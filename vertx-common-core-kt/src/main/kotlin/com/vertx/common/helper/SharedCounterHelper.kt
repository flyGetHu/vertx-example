package com.vertx.common.helper

import cn.hutool.log.StaticLog
import com.vertx.common.config.sharedData
import com.vertx.common.enums.SharedCounterEnum
import io.vertx.kotlin.coroutines.await

/**
 * 事件总线服务调用方
 * 分布式计数器
 */
object SharedCounterHelper {

    /**
     * 获取计数器
     */
    suspend fun getCounterNum(sharedCounterEnum: SharedCounterEnum): Long? {
        return sharedData.getCounter(sharedCounterEnum.key).await().get().await()
    }

    /**
     * 递增计数器后获取计数器
     * @param sharedCounterEnum 计数器枚举
     * @param num 计数器值,需要大于0
     */
    suspend fun getCounterAndAdd(sharedCounterEnum: SharedCounterEnum, num: Long = 1): Long? {
        if (num <= 0) {
            StaticLog.warn("计数器值必须大于0")
            return null
        }
        return sharedData.getCounter(sharedCounterEnum.key).await().addAndGet(num).await()
    }

    /**
     * 递减计数器后获取计数器
     * @param sharedCounterEnum 计数器枚举
     */
    suspend fun getCounterAndDec(sharedCounterEnum: SharedCounterEnum): Long? {
        return sharedData.getCounter(sharedCounterEnum.key).await().decrementAndGet().await()
    }
}