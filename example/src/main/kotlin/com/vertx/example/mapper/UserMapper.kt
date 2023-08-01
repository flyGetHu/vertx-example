package com.vertx.example.mapper

import com.vertx.common.model.User
import com.vertx.common.utils.underlineName
import com.vertx.mysql.helper.MysqlHelper
import org.jooq.impl.DSL

object UserMapper {

    suspend fun list(limit: Int): List<User> {
        // where条件构造
        val where = DSL.noCondition()
        return MysqlHelper.select(
            User::class.java,
            where,
            User::class.java.declaredFields.map { it.name }.toTypedArray().toList(),
            lastSql = " limit $limit"
        )
    }

    suspend fun detail(id: Int): User? {
        // where条件构造
        val where = DSL.field(User::id.name.underlineName()).eq(id)
        return MysqlHelper.select(
            User::class.java,
            where,
            User::class.java.declaredFields.map { it.name }.toTypedArray().toList(),
            lastSql = " limit 1"
        ).firstOrNull()
    }

    suspend fun deleteById(id: Int): Int {
        // where条件构造
        val where = DSL.field(User::id.name.underlineName()).eq(id)
        return MysqlHelper.delete(
            User::class.java,
            where,
        )
    }

    suspend fun updateById(id: Int, name: String): Int {
        // where条件构造
        val where = DSL.field(User::id.name.underlineName()).eq(id)
        // 更新数据
        val data = User(id, name, 0, null, null)
        return MysqlHelper.update(
            data,
            where,
        )
    }

    suspend fun insert(user: User): Long {
        // 插入数据
        return MysqlHelper.insert(
            user,
        )
    }
}