package com.vertx.common.bus

import cn.hutool.http.HttpStatus
import cn.hutool.log.StaticLog
import com.vertx.common.config.eventBus
import com.vertx.common.config.vertx
import io.vertx.core.*
import io.vertx.core.eventbus.Message
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * EventBusHandler is a service that can be used to handle requests on the event bus.
 * 使用方式:
 * 1: 先在本目录下创建一个服务实现类,实现EventBusHandler接口,只需要实现address,确定服务地址,文件命名以BusHandler结尾
 * 2: 然后在具体的业务模块实现rpc接口的逻辑handleRequest,文件BusHandlerImpl结尾
 * 3: 其他模块调用该服务,只需要调用本目录下对应实现的call方法即可
 * @param Request 请求类型
 * @param Response 响应类型
 * @property address 服务地址
 * @constructor
 */
interface BusHandler<Request, Response> {


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
        eventBus.request(this.address, request) { ar: AsyncResult<Message<Response>> ->
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
        fun <Request, Response> register(service: BusHandler<Request, Response>) {
            StaticLog.info("注册服务: ${service.address}")
            eventBus.consumer(service.address) { message: Message<Request> ->
                CoroutineScope(vertx.dispatcher()).launch {
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


