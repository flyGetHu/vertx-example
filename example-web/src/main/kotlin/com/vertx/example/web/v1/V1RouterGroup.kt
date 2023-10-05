package com.vertx.example.web.v1

import com.vertx.common.config.vertx
import com.vertx.example.web.v1.router.*
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
        // mysql路由
        MysqlExampleRouter.init(routerV1)
        // 测试eventbus路由
        TestBusRouter.init(routerV1)
        // redis路由
        RedisExampleRouter.init(routerV1)
        // breaker路由
        BreakerRouter.init(routerV1)
        // 1.0版本路由 挂载到主路由
        router.route("/customer/v1/*").subRouter(routerV1)
    }
}