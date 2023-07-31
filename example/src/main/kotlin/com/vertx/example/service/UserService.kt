package com.vertx.example.service

import com.vertx.example.mapper.UserMapper
import com.vertx.example.model.User

object UserService {

    suspend fun list(): List<User> {
        return UserMapper.list()
    }
}