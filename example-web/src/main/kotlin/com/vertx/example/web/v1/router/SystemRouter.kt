package com.vertx.example.web.v1.router

import com.vertx.common.config.appConfig
import com.vertx.webserver.helper.launchCoroutine
import com.vertx.webserver.helper.successResponse
import io.vertx.ext.web.Router

/**
 * 系统路由
 */
object SystemRouter {

    /**
     * 初始化路由
     */
    fun init(router: Router) {
        // 获取系统配置
        router.get("/app/config").launchCoroutine {
            it.successResponse(appConfig)
        }
    }
}