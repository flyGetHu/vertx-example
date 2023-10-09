package com.vertx.common.enums

/**
 * 表示共享锁对象的枚举。
 *
 * @property key 与共享锁关联的密钥。
 */
enum class SharedLockSharedLockEnum(var key: String) : ISharedLockSharedLockEnum {
    TEST_SHARED_LOCK("test_lock") {
        override fun key(): String {
            return key
        }
    },
}