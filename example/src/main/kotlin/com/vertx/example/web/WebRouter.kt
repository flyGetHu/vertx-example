package com.vertx.example.web

import io.vertx.ext.web.Router

/**
 * 路由配置初始化主类
 */
object WebRouter {

    /**
     * 初始化路由
     */
    fun init(router: Router) {
        ExampleRouter.init(router)
    }
}