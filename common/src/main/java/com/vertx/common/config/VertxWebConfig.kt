package com.vertx.common.config

import cn.hutool.http.HttpStatus
import com.vertx.common.entity.ApiError
import com.vertx.common.entity.ApiResponse
import com.vertx.common.handler.RequestInterceptor
import io.vertx.core.json.Json
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// 扩展函数，用于在 RoutingContext 中启动协程
fun Route.launchCoroutine(fn: suspend (RoutingContext) -> Unit) {
    handler { ctx ->
        CoroutineScope(vertx.dispatcher()).launch {
            try {
                fn(ctx)
            } catch (e: Exception) {
                // 处理协程中的异常
                ctx.fail(e)
            }
        }
    }
}

// 扩展函数，用于发送 JSON 响应
fun RoutingContext.jsonResponse(response: Any) {
    this.response().putHeader("Content-Type", "application/json").end(Json.encodePrettily(response))
}


fun RoutingContext.successResponse(data: Any) {
    val response = ApiResponse(success = true, data = data)
    jsonResponse(response)
}

fun RoutingContext.errorResponse(code: Int = HttpStatus.HTTP_BAD_REQUEST, message: String) {
    val error = ApiError(code, message)
    val response = ApiResponse(success = false, data = null, error = error)
    jsonResponse(response)
}

object VertxWebConfig {
    /**
     * 启动httpserver
     * @param initRouter 初始化路由主函数
     */
    fun startHttpServer(
        initRouter: io.vertx.ext.web.Router.() -> Unit
    ) {
        val httpServerOptions = io.vertx.core.http.HttpServerOptions()
        val serverConfig = appConfig.webServer
        httpServerOptions.port = serverConfig.port
        httpServerOptions.host = serverConfig.host
        httpServerOptions.idleTimeout = serverConfig.timeout
        httpServerOptions.alpnVersions = serverConfig.alpnVersions
        val httpServer = vertx.createHttpServer(httpServerOptions)
        val mainRouter = io.vertx.ext.web.Router.router(vertx)
        mainRouter.route("/*")
            // 添加跨域处理
            .handler(io.vertx.ext.web.handler.CorsHandler.create())
            // 添加日志记录
            .handler {
                if (serverConfig.logEnabled) {
                    io.vertx.ext.web.handler.LoggerHandler.create(io.vertx.ext.web.handler.LoggerFormat.DEFAULT)
                        .handle(it)
                } else {
                    it.next()
                }
            }
            // 添加请求拦截器
            .handler(RequestInterceptor())
        val router = io.vertx.ext.web.Router.router(vertx)
        // 初始化路由
        initRouter(router)
        mainRouter.route(serverConfig.prefix).subRouter(router)
        // 统一处理异常
        mainRouter.errorHandler(HttpStatus.HTTP_INTERNAL_ERROR) { context: RoutingContext ->
            cn.hutool.log.StaticLog.error(context.failure(), "接口异常:{}", context.request().path())
            context.errorResponse(message = "接口异常")
        }
        // 超时异常处理
        mainRouter.errorHandler(HttpStatus.HTTP_UNAVAILABLE) { context: RoutingContext ->
            cn.hutool.log.StaticLog.error(context.failure(), "接口超时:{}", context.request().path())
            context.errorResponse(message = "接口超时")
        }
        // 404异常处理
        mainRouter.errorHandler(HttpStatus.HTTP_NOT_FOUND) { context: RoutingContext ->
            cn.hutool.log.StaticLog.error(
                context.failure(), "接口不存在{}:{}", context.request().method(), context.request().path()
            )
            context.errorResponse(message = "接口不存在")
        }
        // 405异常处理
        mainRouter.errorHandler(HttpStatus.HTTP_BAD_METHOD) { context: RoutingContext ->
            cn.hutool.log.StaticLog.error(
                context.failure(), "接口不支持该方法{}:{}", context.request().method(), context.request().path()
            )
            context.errorResponse(message = "接口不支持该方法")
        }
        httpServer.requestHandler(mainRouter).listen(serverConfig.port)
        cn.hutool.log.StaticLog.info("Web服务端启动成功:端口:${serverConfig.port}")
    }
}