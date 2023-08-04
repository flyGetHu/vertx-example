package com.vertx.example.service

import com.vertx.common.model.User
import com.vertx.example.mapper.UserMapper
import com.vertx.rabbitmq.enums.RabbitMqExChangeEnum
import com.vertx.rabbitmq.helper.RabbitMqHelper

object UserService {

    suspend fun list(limit: Int): List<User> {
        val users = UserMapper.list(limit)
        for (user in users) {
            RabbitMqHelper.sendMessageToExchange(
                RabbitMqExChangeEnum.TESTRabbitMqExChangeEnum,
                user,
                persistenceMessage = null
            )
        }
        return users
    }

    suspend fun page(page: Int, pageSize: Int): List<User> {
        return UserMapper.page(page, pageSize)
    }

    suspend fun detail(id: Int): User? {
        return UserMapper.detail(id)
    }

    suspend fun deleteById(id: Int): Int {
        return UserMapper.deleteById(id)
    }

    suspend fun updateById(id: Int, name: String): Int {
        return UserMapper.updateById(id, name)
    }

    suspend fun insert(user: User): Long {
        return UserMapper.insert(user)
    }

    suspend fun insertBatch(userList: List<User>): Int {
        return UserMapper.insertBatch(userList)
    }
}