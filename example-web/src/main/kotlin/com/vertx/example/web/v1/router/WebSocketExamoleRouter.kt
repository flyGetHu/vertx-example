package com.vertx.example.web.v1.router

import cn.hutool.core.util.StrUtil
import cn.hutool.log.StaticLog
import com.vertx.common.config.eventBus
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

object WebSocketExamoleRouter {
    private val webSocketMap: MutableMap<String, ServerWebSocket> = HashMap()
    fun init(router: Router) {
        router.route("/ws").handler { event: RoutingContext ->
            val request = event.request()
            request.toWebSocket().onSuccess { serverWebSocket: ServerWebSocket ->
                println("Socket connected!")
                serverWebSocket.textMessageHandler { data: String ->
                    val address = serverWebSocket.textHandlerID()
                    val jsonObject = JsonObject(data)
                    val path = jsonObject.getString("path")
                    val cosuolId = jsonObject.getString("cosuolId")
                    val dataInfo = jsonObject.getJsonObject("data")
                    println("Received data: $data")
                    if (StrUtil.isNotBlank(cosuolId)) {
                        webSocketMap[cosuolId] = serverWebSocket
                    } else {
                        eventBus.send(path, data)
                    }
                    when (path) {
                        "/hello" -> println("Received hello message!")
                        else -> println("Received default message!")
                    }
                    println("Received dataInfo: $dataInfo")
                    serverWebSocket.writeTextMessage("Hello from server!")
                    eventBus.send(address, data)
                }
                serverWebSocket.closeHandler { event1: Void? -> println("Socket closed!") }
                serverWebSocket.exceptionHandler { event1: Throwable -> println("Socket exception: " + event1.message) }
                serverWebSocket.pongHandler { event1: Buffer? -> println("Received pong message!") }
            }.onFailure {
                StaticLog.error(it, "简历websocket连接失败")
            }
        }
    }
}
