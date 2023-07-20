package com.vertx.example.web.v1

import com.vertx.common.config.vertx
import com.vertx.example.web.v1.router.SystemRouter
import com.vertx.example.web.v1.router.WebSocketExamoleRouter
import io.vertx.ext.web.Router

/**
 * v1版本路由组
 */
object V1RouterGroup {

    fun init(router: Router) {
        // 1.0版本路由
        val routerV1 = Router.router(vertx)
        // 系统路由
        SystemRouter.init(routerV1)
        // websocket路由
        WebSocketExamoleRouter.init(routerV1)
        // 1.0版本路由 挂载到主路由
        router.route("/v1/*").subRouter(routerV1)
    }
}