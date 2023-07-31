package com.vertx.example.mapper

import com.vertx.common.client.MysqlClient
import com.vertx.common.utils.underlineName
import com.vertx.example.model.User
import org.jooq.impl.DSL

object UserMapper {

    suspend fun list(id: Int): List<User> {
        // where条件构造
        val where = DSL.field(User::id.name.underlineName()).eq(id)
        return MysqlClient.select(
            User::class.java, where, User::class.java.declaredFields.map { it.name }.toTypedArray().toList()
        )
    }

    suspend fun deleteById(id: Int): Int {
        // where条件构造
        val where = DSL.field(User::id.name.underlineName()).eq(id)
        return MysqlClient.delete(
            User::class.java,
            where,
        )
    }

    suspend fun updateById(id: Int, name: String): Int {
        // where条件构造
        val where = DSL.field(User::id.name.underlineName()).eq(id)
        // 更新数据
        val data = User(id, name, 0, null, null)
        return MysqlClient.update(
            data,
            where,
        )
    }

    suspend fun insert(user: User): Long {
        // 插入数据
        return MysqlClient.insert(
            user,
        )
    }
}