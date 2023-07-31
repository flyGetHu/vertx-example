package com.vertx.example.web.v1.router

import com.vertx.common.config.launchCoroutine
import com.vertx.common.config.successResponse
import com.vertx.common.config.vertx
import com.vertx.example.model.User
import com.vertx.example.service.UserService
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

object MysqlExampleRouter {

    fun init(router: Router) {
        val routerSub = Router.router(vertx)

        routerSub.get("/list/:limit").launchCoroutine {
            val limit = it.pathParam("limit").toInt()
            val list = UserService.list(limit)
            it.successResponse(list)
        }


        routerSub.get("/detail/:id").launchCoroutine {
            val id = it.pathParam("id").toInt()
            val user = UserService.detail(id)
            if (user == null) {
                it.response().setStatusCode(404).end("user not found")
            } else {
                it.successResponse(user)
            }
        }

        routerSub.delete("/delete/:id").launchCoroutine {
            val id = it.pathParam("id").toInt()
            val result = UserService.deleteById(id)
            it.successResponse(result)
        }

        routerSub.put("/update/:id").launchCoroutine {
            val id = it.pathParam("id").toInt()
            val name = it.request().getParam("name")
            val result = UserService.updateById(id, name)
            it.successResponse(result)
        }

        routerSub.post("/insert").handler(BodyHandler.create()).launchCoroutine {
            val user = it.body().asJsonObject().mapTo(User::class.java)
            val res = UserService.insert(user)
            it.successResponse(res)
        }
        router.route("/user/*").subRouter(routerSub)
    }
}