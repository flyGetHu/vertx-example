package com.vertx.common.bus

import cn.hutool.http.HttpStatus
import cn.hutool.log.StaticLog
import io.vertx.core.*
import io.vertx.core.eventbus.Message
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * EventBusHandler is a service that can be used to handle requests on the event bus.
 * @param Request 请求类型
 * @param Response 响应类型
 * @see MyEventBusServiceImplTest.kt 有具体示例
 */
interface EventBusHandler<Request, Response> {


    /**
     * The address the service is registered on.
     */
    val address: String

    /**
     * 服务提供方处理请求
     */
    suspend fun handleRequest(request: Request, resultHandler: Handler<AsyncResult<Response>>)

    /**
     * Call the service
     * @param request the request to send
     * @return a future for the result
     */
    fun call(request: Request): Future<Response> {
        val promise = Promise.promise<Response>()
        Vertx.currentContext().owner().eventBus().request(this.address, request) { ar: AsyncResult<Message<Response>> ->
            if (ar.succeeded()) {
                val body = ar.result().body()
                promise.complete(body)
            } else {
                promise.fail(ar.cause())
                StaticLog.error(ar.cause(), "${this.address}服务调用失败")
            }
        }
        return promise.future()
    }

    companion object {
        /**
         * 注册服务
         * @param service the service to register
         * @return a future for the result
         * @param Request 请求类型
         * @param Response 响应类型
         */
        fun <Request, Response> register(service: EventBusHandler<Request, Response>) {
            StaticLog.info("注册服务: ${service.address}")
            Vertx.currentContext().owner().eventBus().consumer(service.address) { message: Message<Request> ->
                CoroutineScope(Vertx.currentContext().dispatcher()).launch {
                    val request = message.body()
                    service.handleRequest(request) { ar: AsyncResult<Response> ->
                        if (ar.succeeded()) {
                            message.reply(ar.result())
                        } else {
                            message.fail(HttpStatus.HTTP_INTERNAL_ERROR, ar.cause().message)
                            StaticLog.error(ar.cause(), "${service.address}服务处理失败")
                        }
                    }
                }
            }
        }
    }
}
