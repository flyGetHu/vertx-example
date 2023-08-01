package com.vertx.example.mapper

import com.vertx.common.model.User
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
}