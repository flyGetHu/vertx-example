package com.vertx.example.service

import com.vertx.common.model.User
import com.vertx.example.mapper.UserMapper

object UserService {

    suspend fun list(limit: Int): List<User> {
        return UserMapper.list(limit)
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
}