package com.vertx.example.web.v1.router

import com.vertx.example.service.TestBusService
import com.vertx.webserver.helper.launchCoroutine
import com.vertx.webserver.helper.successResponse
import io.vertx.ext.web.Router

object TestBusRouter {

    fun init(router: Router) {
        router.get("/test/bus").launchCoroutine { ctx ->
            val res = TestBusService.testBus()
            ctx.successResponse(res)
        }
    }
}