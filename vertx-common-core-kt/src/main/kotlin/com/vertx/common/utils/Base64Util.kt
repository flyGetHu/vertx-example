package com.vertx.common.utils

import cn.hutool.core.codec.Base64
import cn.hutool.log.StaticLog
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * base64工具类
 * 用于base64与图片之间的转换
 * @param base64Str base64字符串
 * @param path 图片路径
 * @return 转换结果
 */
fun Base64.base64ToImage(base64Str: String, outputPath: String): Boolean {
    if (base64Str.isBlank()) {
        StaticLog.warn("base64 conversion to image failed: base64 is blank")
        return false
    }
    if (outputPath.isBlank()) {
        StaticLog.warn("base64 conversion to image failed: output path is blank")
        return false
    }
    return try {
        val bytes = Base64.decode(base64Str)
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