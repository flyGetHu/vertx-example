package com.vertx.webserver.entity

import com.vertx.webserver.handler.RequestInterceptorHandler
import com.vertx.webserver.handler.RequestInterceptorHandlerImpl
import io.vertx.ext.web.Router

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
    var requestInterceptorHandler: RequestInterceptorHandler = RequestInterceptorHandlerImpl()
}