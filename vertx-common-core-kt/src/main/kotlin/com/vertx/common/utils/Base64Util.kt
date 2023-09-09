package com.vertx.common.utils

import cn.hutool.core.codec.Base64
import cn.hutool.log.StaticLog
import java.io.FileOutputStream

/**
 * base64工具类
 * 用于base64与图片之间的转换
 * @param base64Str base64字符串
 * @param path 图片路径
 * @return 转换结果
 */
fun Base64.base64ToImage(base64Str: String, path: String): Boolean {
    //对字节数组字符串进行Base64解码并生成图片
    //图像数据为空
    if (base64Str.isEmpty()) {
        StaticLog.error("base64转图片失败：base64为空")
        return false
    }
    try {
        //Base64解码
        val bytes = Base64.decode(base64Str)
        for (i in bytes.indices) {
            //调整异常数据
            if (bytes[i] < 0) {
                bytes[i] = (bytes[i] + 256).toByte()
            }
        }
        //生成jpeg图片
        val out = FileOutputStream(path)
        out.write(bytes)
        out.flush()
        out.close()
    } catch (e: Exception) {
        StaticLog.error(e, "base64转图片失败")
        return false
    }
    return true
}