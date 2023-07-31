package com.vertx.common.utils

import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(VertxExtension::class)
class MysqlUtilTest {
    class TestEntity {
        var id: Int = 0
        var name: String = ""
        var age: Int? = 0
        var createTime: LocalDateTime = LocalDateTime.now()
        var updateTime: LocalDateTime = LocalDateTime.now()
    }
}