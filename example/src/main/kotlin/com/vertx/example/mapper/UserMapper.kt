package com.vertx.example.mapper

import com.vertx.common.client.MysqlClient
import com.vertx.example.model.User
import org.jooq.impl.DSL

object UserMapper {

    suspend fun list(): List<User> {
        return MysqlClient.select(
            User::class.java,
            DSL.condition(DSL.field(User::id.name).eq(1)),
            listOf(User::id.name)
        )
    }
}