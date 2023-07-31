package com.vertx.example.web.v1.router

import com.vertx.common.config.launchCoroutine
import com.vertx.common.config.successResponse
import com.vertx.example.service.UserService
import io.vertx.ext.web.Router

object MysqlExampleRouter {

    fun init(router: Router) {
        router.get("/user/list").launchCoroutine {
            val userList = UserService.list()
            it.successResponse(userList)
        }
    }
}