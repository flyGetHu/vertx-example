package com.vertx.common.entity.mysql

/**
 * 表示包含分页数据的页面响应。
 *
 * @param T 页面响应中包含的数据类型
 * @property pageNumber 当前页码
 * @property pageSize 每页的项目数
 * @property totalItems 所有页面的项目总数
 * @property totalPages 总页数
 * @property data 本页数据项列表
 */
data class PageResult<T>(
    val pageNumber: Int, val pageSize: Int, val totalItems: Long, val totalPages: Int, val data: List<T>
)
