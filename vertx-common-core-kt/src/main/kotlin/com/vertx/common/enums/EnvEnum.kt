package com.vertx.common.enums

/**
 * 代表不同环境的枚举。
 *
 * @property env 环境的名称。
 * @property description 环境的简要描述。
 */
enum class EnvEnum(val env: String, val description: String) {
    DEV("dev", "开发环境"),
    TEST("test", "测试环境"),
    PROD("prod", "生产环境"),
}