package com.vertx.example.handler

import com.vertx.common.config.appConfig
import com.vertx.common.config.vertx
import com.vertx.redis.helper.RedisHelper
import com.vertx.webserver.helper.errorResponse
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RequestInterceptorHandler : Handler<RoutingContext> {
    override fun handle(context: RoutingContext) {
        val webServer = appConfig.webServer ?: throw Exception("未配置webserver")
        val ignorePaths = webServer.ignorePaths
        val request = context.request()
        val path = request.path()
        //对指定请求不进行拦截
        if (ignorePaths.contains(path)) {
            context.next()
            return
        }
        //校验basic auth:username/password
        val authHeader = request.getHeader("Authorization")
        if (authHeader.isNullOrBlank()) {
            context.errorResponse(code = 401, message = "basic auth:Authorization is null or blank")
            return
        }
        val auth = authHeader.split(" ")
        if (auth.size != 2 || auth[0] != "Basic") {
            context.errorResponse(code = 401, message = "basic auth:Authorization is error")
            return
        }
        val usernamePassword = String(java.util.Base64.getDecoder().decode(auth[1])).split(":")
        val username = usernamePassword[0]
        val password = usernamePassword[1]
        if (username.isBlank() || password.isBlank()) {
            context.errorResponse(code = 401, message = "basic auth:username/password is null or blank")
            return
        }
        CoroutineScope(vertx.dispatcher()).launch {
            val authPassword = RedisHelper.Hash.hget("user:basic auth", username)
            if (authPassword.isNullOrBlank() || authPassword != password) {
                context.errorResponse(code = 401, message = "basic auth:username/password is error")
            } else {
                context.next()
            }
        }
    }
}