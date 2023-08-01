package com.vertx.example.web

import com.vertx.example.web.v1.V1RouterGroup
import io.vertx.ext.web.Router

/**
 * 路由配置初始化主类
 */
object WebRouter {

    /**
     * 初始化路由
     */
    fun init(router: Router) {
        V1RouterGroup.init(router)
    }
}