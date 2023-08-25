package com.vertx.common.enums

enum class EnvEnum(val env: String, val description: String) {
    DEV("dev", "开发环境"),
    TEST("test", "测试环境"),
    PROD("prod", "生产环境"),
}