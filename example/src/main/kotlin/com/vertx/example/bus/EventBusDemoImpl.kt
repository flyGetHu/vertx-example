package com.vertx.example.bus

import com.vertx.common.bus.EventBusHandler
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

object EventBusDemoImpl : EventBusHandler<String, String> {
    override val address: String = "demo://eventbus"

    override suspend fun handleRequest(request: String, resultHandler: Handler<AsyncResult<String>>) {
        resultHandler.handle(io.vertx.core.Future.succeededFuture("hello $request"))
    }
}