package com.vertx.common.utils


/**
 * 驼峰转下划线
 * 如果转换结果以_开头，则去掉
 * @param name 驼峰格式
 */
fun underlineName(name: String): String {
    val sb = StringBuilder()
    for (c in name.toCharArray()) {
        if (Character.isUpperCase(c)) {
            sb.append("_")
            sb.append(Character.toLowerCase(c))
        } else {
            sb.append(c)
        }
    }
    if (sb[0] == '_') {
        sb.deleteCharAt(0)
    }
    return sb.toString()
}


