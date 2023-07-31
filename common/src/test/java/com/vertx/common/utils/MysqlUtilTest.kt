package com.vertx.common.utils

import io.vertx.junit5.VertxExtension
import org.jooq.impl.DSL
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
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

    @Test
    fun test1() {
        val testEntity = TestEntity()
        testEntity.id = 1
        testEntity.name = "test"
        testEntity.age = 18
        val sql = MysqlUtil.buildInsertSql(testEntity)
        println(sql)
    }

    @Test
    fun test2() {
        val testEntity = TestEntity()
        testEntity.id = 1
        testEntity.name = "test"
        testEntity.age = 18
        val sql = MysqlUtil.buildUpdateSql(testEntity, DSL.condition(DSL.field("id").eq(1)))
        println(sql)
    }

    @Test
    fun test3() {
        val testEntity = TestEntity()
        testEntity.id = 1
        testEntity.name = "test"
        testEntity.age = 18
        val where = DSL.condition(DSL.field("id").eq(1))
        val sql = MysqlUtil.buildSelectSql(TestEntity::class.java, where, listOf(TestEntity::id.name))
        println(sql)
    }

    @Test
    fun test4() {
        val testEntity = TestEntity()
        testEntity.id = 1
        testEntity.name = "test"
        testEntity.age = 18
        val where = DSL.condition(DSL.field("id").eq(1))
        val sql = MysqlUtil.buildDeleteSql(TestEntity::class.java, where)
        println(sql)
    }
}