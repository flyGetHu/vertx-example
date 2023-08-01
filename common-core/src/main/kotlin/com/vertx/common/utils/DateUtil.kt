package com.vertx.common.utils

import cn.hutool.core.date.DateTime
import cn.hutool.core.date.DateUtil

/**
 * 定义序列化字符串时间格式
 * 需要考虑兼容多种格式
 */
fun parseDate(dateTime: String): DateTime {
    return DateUtil.parse(
        dateTime.split(".")[0],
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm",
        "yyyy-MM-dd",
    )
}