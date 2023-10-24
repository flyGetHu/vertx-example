package com.vertx.common.helper

import com.vertx.common.config.sharedData
import com.vertx.common.enums.ISharedLockSharedLockEnum
import io.vertx.core.shareddata.Lock
import io.vertx.kotlin.coroutines.await

/**
 * 分布式锁
 */
object SharedLockHelper {

    /**
     * 获取锁
     * @param sharedLockEnum 锁枚举
     */
    suspend fun getLock(sharedLockEnum: ISharedLockSharedLockEnum): Lock? {
        return sharedData.getLock(sharedLockEnum.key).await()
    }

    /**
     * 获取锁
     * @param sharedLockEnum 锁枚举
     * @param timeout 超时时间
     */
    suspend fun getLockWithTimeout(sharedLockEnum: ISharedLockSharedLockEnum, timeout: Long): Lock? {
        return sharedData.getLockWithTimeout(sharedLockEnum.key, timeout).await()
    }

    /**
     * 获取本地锁
     * @param sharedLockEnum 锁枚举
     */
    suspend fun getLocalLock(sharedLockEnum: ISharedLockSharedLockEnum): Lock? {
        return sharedData.getLocalLock(sharedLockEnum.key).await()
    }

    /**
     * 获取本地锁
     * @param sharedLockEnum 锁枚举
     * @param timeout 超时时间
     */
    suspend fun getLocalLockWithTimeout(sharedLockEnum: ISharedLockSharedLockEnum, timeout: Long): Lock? {
        return sharedData.getLocalLockWithTimeout(sharedLockEnum.key, timeout).await()
    }
}