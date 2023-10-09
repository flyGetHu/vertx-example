package com.vertx.common.utils

import cn.hutool.core.codec.Base64
import cn.hutool.log.StaticLog
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * 驼峰转下划线
 * 如果转换结果以_开头，则去掉
 */
fun String.underlineName(): String {
    val sb = StringBuilder()
    for (c in this.toCharArray()) {
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


/**
 * 将base64编码的字符串转换为图像文件并保存到指定的输出路径。
 *
 * @param outputPath 图像文件保存的路径
 * 如果base64转换和图像文件保存成功，则返回`true`，否则返回`false`
 */
fun String.base64ToImage(outputPath: String): Boolean {
    if (this.isBlank()) {
        StaticLog.warn("base64 conversion to image failed: base64 is blank")
        return false
    }
    if (outputPath.isBlank()) {
        StaticLog.warn("base64 conversion to image failed: output path is blank")
        return false
    }
    return try {
        val bytes = Base64.decode(this)
        val path: Path = Paths.get(outputPath)

        Files.newOutputStream(path).use { os ->
            os.write(bytes)
        }
        true
    } catch (e: Exception) {
        StaticLog.error(e, "base64 conversion to image failed with error: ")
        false
    }
}