package com.vertx.mysql.helper

import cn.hutool.log.StaticLog
import com.vertx.common.model.User
import org.junit.jupiter.api.Test

object MysqlHelperTest {
    @Test
    fun testInsert() {
        val user = User(12, "2", 12, "222", "222")
        try {
            user.javaClass.getDeclaredField("sss")
        } catch (_: NoSuchFieldException) {
            StaticLog.warn("对象${user::class.java.name}不存在id主键")
            0
        }
    }
}