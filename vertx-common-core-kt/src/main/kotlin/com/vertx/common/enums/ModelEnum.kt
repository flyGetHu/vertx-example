package com.vertx.common.enums

/**
 * 业务模块枚举
 * @param modelName 模块名称
 * @param description 模块描述
 */
enum class ModelEnum(override var modelName: String, override var description: String) : IModelEnum {
    TEST__MODEL("test", "测试模块"),
}