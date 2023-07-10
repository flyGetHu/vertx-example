package com.vertx.example.bus

import com.vertx.common.bus.DemoBusHandler
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

object DemoBusHandlerImpl : DemoBusHandler() {

    override suspend fun handleRequest(request: String, resultHandler: Handler<AsyncResult<String>>) {
        resultHandler.handle(io.vertx.core.Future.succeededFuture("hello $request"))
    }
}