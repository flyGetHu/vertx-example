package com.vertx.webserver.handler

import io.vertx.ext.web.RoutingContext

interface RequestInterceptorHandler {

    /**
     * 请求拦截器
     * @param context RoutingContext
     * @return String 返回空字符串则代表请求通过,否则返回错误信息
     */
    suspend fun handle(context: RoutingContext): String
}