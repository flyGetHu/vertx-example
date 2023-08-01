package com.vertx.mysql.config

/**
 * 表名注解
 * 如果没有TableName注解，则使用类名转下划线的结果
 * @param name 表名
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TableName(val name: String)
