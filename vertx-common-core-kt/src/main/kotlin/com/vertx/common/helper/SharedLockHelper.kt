package com.vertx.common.helper

import cn.hutool.core.util.StrUtil
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
     * @param args 参数  如果有参数则使用{}占位符，如：getLock(SharedLockEnum.TEST_LOCK, arrayOf("123"))
     * @return Lock
     */
    suspend fun getLock(sharedLockEnum: ISharedLockSharedLockEnum, args: Array<String>? = null): Lock {
        return sharedData.getLock(StrUtil.format(sharedLockEnum.key, args)).await()
    }

    suspend fun withLock(
        sharedLockEnum: ISharedLockSharedLockEnum,
        args: Array<String>? = null,
        block: suspend () -> Unit
    ) {
        val lock = getLock(sharedLockEnum, args)
        try {
            block()
        } finally {
            lock.release()
        }
    }

    /**
     * 获取锁
     * @param sharedLockEnum 锁枚举
     * @param timeout 超时时间
     */
    suspend fun getLockWithTimeout(
        sharedLockEnum: ISharedLockSharedLockEnum,
        timeout: Long,
        args: Array<String>?
    ): Lock {
        return sharedData.getLockWithTimeout(StrUtil.format(sharedLockEnum.key, args), timeout).await()
    }

    suspend fun withLockWithTimeout(
        sharedLockEnum: ISharedLockSharedLockEnum,
        timeout: Long,
        args: Array<String>? = null,
        block: suspend () -> Unit
    ) {
        val lock = getLockWithTimeout(sharedLockEnum, timeout, args)
        try {
            block()
        } finally {
            lock.release()
        }
    }

    /**
     * 获取本地锁
     * @param sharedLockEnum 锁枚举
     */
    suspend fun getLocalLock(sharedLockEnum: ISharedLockSharedLockEnum, args: Array<String>? = null): Lock {
        return sharedData.getLocalLock(StrUtil.format(sharedLockEnum.key, args)).await()
    }

    suspend fun withLocalLock(
        sharedLockEnum: ISharedLockSharedLockEnum,
        args: Array<String>? = null,
        block: suspend () -> Unit
    ) {
        val lock = getLocalLock(sharedLockEnum, args)
        try {
            block()
        } finally {
            lock.release()
        }
    }

    /**
     * 获取本地锁
     * @param sharedLockEnum 锁枚举
     * @param timeout 超时时间
     */
    suspend fun getLocalLockWithTimeout(
        sharedLockEnum: ISharedLockSharedLockEnum,
        timeout: Long,
        args: Array<String>? = null
    ): Lock {
        return sharedData.getLocalLockWithTimeout(StrUtil.format(sharedLockEnum.key, args), timeout)
            .await()
    }


    suspend fun withLocalLockWithTimeout(
        sharedLockEnum: ISharedLockSharedLockEnum,
        timeout: Long,
        args: Array<String>? = null,
        block: suspend () -> Unit
    ) {
        val lock = getLocalLockWithTimeout(sharedLockEnum, timeout, args)
        try {
            block()
        } finally {
            lock.release()
        }
    }
}