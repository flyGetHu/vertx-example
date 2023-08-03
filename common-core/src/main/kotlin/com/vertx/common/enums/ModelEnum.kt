package com.vertx.common.enums

/**
 * 模型枚举
 * @param modelName 模块名称
 * @param description 模块描述
 */
enum class ModelEnum(val modelName: String, val description: String) {
    /**
     * 用户模块
     */
    USER("user", "用户模块"),

    /**
     * 角色模块
     */
    ROLE("role", "角色模块"),

    /**
     * 权限模块
     */
    PERMISSION("permission", "权限模块"),

    /**
     * 菜单模块
     */
    MENU("menu", "菜单模块"),

    /**
     * 部门模块
     */
    DEPARTMENT("department", "部门模块"),

    /**
     * 字典模块
     */
    DICTIONARY("dictionary", "字典模块"),

    /**
     * 日志模块
     */
    LOG("log", "日志模块"),

    /**
     * 通知模块
     */
    NOTICE("notice", "通知模块"),

    /**
     * 通知模块
     */
    MESSAGE("message", "消息模块"),

    /**
     * 通知模块
     */
    FILE("file", "文件模块"),

    /**
     * 通知模块
     */
    TASK("task", "任务模块"),

    /**
     * 通知模块
     */
    DICT("dict", "字典模块"),
}