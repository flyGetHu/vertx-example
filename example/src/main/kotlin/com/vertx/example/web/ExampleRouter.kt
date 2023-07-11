package com.vertx.example.web

import com.vertx.common.config.appConfig
import com.vertx.common.config.launchCoroutine
import com.vertx.common.config.successResponse
import io.vertx.ext.web.Router

object ExampleRouter {

    fun init(router: Router) {
        router.get("/").launchCoroutine {
            it.successResponse(appConfig)
        }
    }
}