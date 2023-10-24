package com.vertx.common.annotations

/**
 * 表示各个接口实现类的中属性地址的注解
 * 确保每个接口实现类中的属性地址都是唯一的
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class UniqueAddress(val value: String = "")