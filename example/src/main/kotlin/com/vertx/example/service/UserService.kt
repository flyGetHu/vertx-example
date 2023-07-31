package com.vertx.example.service

import com.vertx.example.mapper.UserMapper
import com.vertx.example.model.User

object UserService {

    suspend fun list(id: Int): List<User> {
        return UserMapper.list(id)
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