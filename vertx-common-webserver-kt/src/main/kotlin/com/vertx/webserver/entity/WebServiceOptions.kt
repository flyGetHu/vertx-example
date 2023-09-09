package com.vertx.webserver.entity

import com.vertx.webserver.handler.RequestInterceptorHandler
import io.vertx.core.Handler
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

/**
 * WebService configuration class
 */
class WebServiceOptions {
    /**
     * 初始化挂载路由
     */
    var initRouter: (Router) -> Unit = {}

    /**
     * 请求拦截器
     */
    var requestInterceptorHandler: Handler<RoutingContext> = RequestInterceptorHandler()
}