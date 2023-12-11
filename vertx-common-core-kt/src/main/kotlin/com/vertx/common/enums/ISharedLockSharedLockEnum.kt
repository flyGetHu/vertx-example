package com.vertx.common.enums

/**
 * 表示共享锁枚举的接口。
 */
interface ISharedLockSharedLockEnum {
    // 键 如果是多个参数，使用{}包裹，如：key = "user_{}_{}" args = ["1", "2"]
    var key: String
}
