/**
 * Handler for intercepting requests.
 * Can exclude specified request paths from interception.
 * Can intercept requests for purposes such as permission verification, parameter verification, etc.
 */
package com.vertx.common.handler

import com.vertx.common.config.appConfig
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

/**
 * 请求拦截器
 * 可以对指定请求路径不进行拦截
 * 可以对请求进行拦截 如：权限校验,参数校验等
 */
class RequestInterceptorHandler : Handler<RoutingContext> {
    override fun handle(context: RoutingContext) {
        val ignorePaths = appConfig.webServer.ignorePaths
        val path = context.request().path()
        //对指定请求不进行拦截
        if (ignorePaths.contains(path)) {
            context.next()
            return
        }
        //对请求进行拦截 todo
        context.next()
    }
}