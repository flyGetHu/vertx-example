/**
 * This file contains the implementation of the VertxWebConfig class and some extension functions for RoutingContext and Route.
 * It provides functions for starting an HTTP server, handling responses, and logging.
 * The startHttpServer function takes an initRouter function that initializes the main router.
 * The file also defines extension functions for RoutingContext and Route, which allow launching coroutines and sending JSON responses.
 */
package com.vertx.webserver.helper

import cn.hutool.http.HttpStatus
import cn.hutool.log.StaticLog
import com.vertx.common.config.appConfig
import com.vertx.common.config.isInit
import com.vertx.common.config.vertx
import com.vertx.common.entity.web.ApiResponse
import com.vertx.common.enums.ApiResponseStatusEnum
import com.vertx.rabbitmq.exception.WebServerStartException
import com.vertx.webserver.entity.WebServiceOptions
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.LoggerFormat
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// 协程上下文中的key
const val coroutineScope = "coroutineScope"

// 扩展函数，用于在 RoutingContext 中启动协程
fun Route.launchCoroutine(fn: suspend (RoutingContext) -> Unit) {
    handler { ctx ->
        val context = Vertx.currentContext()
        CoroutineScope(vertx.dispatcher()).launch {
            // 将 RoutingContext 传入协程上下文
            context.put(coroutineScope, this)
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
fun RoutingContext.jsonResponse(response: Any?) {
    this.response().putHeader("Content-Type", "application/json").end(Json.encode(response))
}


/**
 * 成功响应
 * @param data 响应数据
 */
fun RoutingContext.successResponse(data: Any?, extra: Any? = null) {
    val response =
        ApiResponse(status = ApiResponseStatusEnum.OK, code = HttpStatus.HTTP_OK, msg = "", data = data, extra = extra)
    jsonResponse(response)
}

/**
 * 失败响应
 * @param code 响应码 默认400
 * @param message 响应消息
 */
fun RoutingContext.errorResponse(code: Int = HttpStatus.HTTP_INTERNAL_ERROR, message: String, extra: Any? = null) {
    val response = ApiResponse(status = ApiResponseStatusEnum.ERROR, code = code, msg = message, extra = extra)
    jsonResponse(response)
}

object VertxWebConfig {
    /**
     * 启动httpserver
     * @param webServiceOptions web服务配置
     */
    fun startHttpServer(webServiceOptions: WebServiceOptions) {
        if (!isInit) {
            StaticLog.error("全局初始化未完成,请先调用:VertxLoadConfig.init()")
            throw WebServerStartException("全局初始化未完成,请先调用:VertxLoadConfig.init()")
        }
        val httpServerOptions = io.vertx.core.http.HttpServerOptions()
        val serverConfig = appConfig.webServer ?: throw WebServerStartException("未配置webserver")
        httpServerOptions.port = serverConfig.port
        httpServerOptions.host = serverConfig.host
        httpServerOptions.idleTimeout = serverConfig.timeout
        httpServerOptions.alpnVersions = serverConfig.alpnVersions
        //websocket 配置 是否注册websocket到eventbus中
        httpServerOptions.setRegisterWebSocketWriteHandlers(true)
        // 开启gzip压缩
        httpServerOptions.isCompressionSupported = serverConfig.compressionSupported
        // 压缩等级
        httpServerOptions.compressionLevel = serverConfig.compressionLevel
        val httpServer = vertx.createHttpServer(httpServerOptions)
        val mainRouter = io.vertx.ext.web.Router.router(vertx)
        val loggerHandler = loggerHandler()
        val corsHandler = io.vertx.ext.web.handler.CorsHandler.create()
        mainRouter.route("/*")
            // 添加跨域处理
            .handler(corsHandler)
            // 添加日志记录
            .handler {
                if (serverConfig.logEnabled) {
                    loggerHandler.handle(it)
                } else {
                    it.next()
                }
            }
        val router = io.vertx.ext.web.Router.router(vertx)
        // 初始化路由
        webServiceOptions.initRouter(router)
        mainRouter.route(serverConfig.prefix).subRouter(router)
        // 统一处理异常
        mainRouter.errorHandler(HttpStatus.HTTP_INTERNAL_ERROR) { context: RoutingContext ->
            StaticLog.error(context.failure(), "接口异常:{}", context.request().path())
            context.errorResponse(code = HttpStatus.HTTP_INTERNAL_ERROR, message = "接口异常")
        }
        // 超时异常处理
        mainRouter.errorHandler(HttpStatus.HTTP_UNAVAILABLE) { context: RoutingContext ->
            StaticLog.error(context.failure(), "接口超时:{}", context.request().path())
            context.errorResponse(code = HttpStatus.HTTP_UNAVAILABLE, message = "接口超时")
        }
        // 404异常处理
        mainRouter.errorHandler(HttpStatus.HTTP_NOT_FOUND) { context: RoutingContext ->
            StaticLog.error(
                context.failure(), "接口不存在{}:{}", context.request().method(), context.request().path()
            )
            context.errorResponse(code = HttpStatus.HTTP_NOT_FOUND, message = "接口不存在")
        }
        // 405异常处理
        mainRouter.errorHandler(HttpStatus.HTTP_BAD_METHOD) { context: RoutingContext ->
            StaticLog.error(
                context.failure(), "接口不支持该方法{}:{}", context.request().method(), context.request().path()
            )
            context.errorResponse(code = HttpStatus.HTTP_BAD_METHOD, message = "接口不支持该方法")
        }
        httpServer.requestHandler(mainRouter).listen(serverConfig.port)
        StaticLog.info("Web服务端启动成功:端口:${serverConfig.port}")
    }

    /**
     * 日志记录
     */
    private fun loggerHandler(): LoggerHandler {
        val loggerHandler = LoggerHandler.create(LoggerFormat.CUSTOM)
        loggerHandler.customFormatter { routingContext, ms ->
            val request = routingContext.request()
            val response = routingContext.response()
            val statusCode = response.statusCode
            val remoteAddress = request.remoteAddress()
            val version = request.version()
            val method = request.method()
            val path = request.path()
            val bytesWritten = request.response().bytesWritten()
            val userAgent = request.headers().get("user-agent")
            // 自定义日志格式 json格式
            val message = """
                    {"remoteAddress":"$remoteAddress","version":"$version","method":"$method","path":"$path","statusCode":$statusCode,"bytesWritten":$bytesWritten,"userAgent":"$userAgent","ms":$ms}
                """.trimIndent()
            message
        }
        return loggerHandler
    }
}