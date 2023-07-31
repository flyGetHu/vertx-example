package com.vertx.example.mapper

import com.vertx.common.client.MysqlClient
import com.vertx.common.utils.underlineName
import com.vertx.example.model.User
import org.jooq.impl.DSL

object UserMapper {

    suspend fun list(): List<User> {
        // where条件构造
        val where = DSL.field(User::id.name.underlineName()).eq(1)
        return MysqlClient.select(
            User::class.java,
            where,
            listOf(User::id.name)
        )
    }
}