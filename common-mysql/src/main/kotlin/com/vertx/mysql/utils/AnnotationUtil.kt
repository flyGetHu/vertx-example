package com.vertx.mysql.utils

import com.vertx.common.config.TableName
import com.vertx.common.utils.underlineName

/**
 * 获取表名
 * 如果没有TableName注解，则使用类名转下划线的结果
 */
fun getTableName(entityClass: Class<*>): String {
    val tableNameAnnotation = entityClass.getAnnotation(TableName::class.java)
    if (tableNameAnnotation == null) {
        val className = entityClass.simpleName
        return className.underlineName()
    }
    return tableNameAnnotation.name
}