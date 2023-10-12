package com.vertx.common.utils

import cn.hutool.core.date.DateException
import cn.hutool.core.date.DateTime
import cn.hutool.core.date.DateUtil

/**
 * 定义序列化字符串时间格式
 * 需要考虑兼容多种格式
 */
fun parseDate(dateTime: String): DateTime? {
    val formats = listOf(
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm",
        "yyyy-MM-dd",
        "yyyyMMdd",
        "yyyyMMddHHmmss",
        "EEE, dd MMM yyyy HH:mm:ss Z", // RFC 822
        "EEEE, dd-MMM-yy HH:mm:ss ZZZ", // RFC 850
        "EEE MMM dd HH:mm:ss yyyy" // ANSI C's asctime()
    )

    for (format in formats) {
        try {
            return DateUtil.parse(dateTime, format)
        } catch (e: IllegalArgumentException) {
            // ignore and try next format
        } catch (e: DateException) {
            // ignore and try next format
        }
    }

    return null
}