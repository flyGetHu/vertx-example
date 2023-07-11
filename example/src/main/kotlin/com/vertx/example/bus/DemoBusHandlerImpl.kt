package com.vertx.example.bus

import cn.hutool.log.StaticLog
import com.vertx.common.bus.DemoBusHandler
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

object DemoBusHandlerImpl : DemoBusHandler() {

    override suspend fun handleRequest(request: String, resultHandler: Handler<AsyncResult<String>>) {
        StaticLog.info("DemoBusHandlerImpl: $request")
        resultHandler.handle(io.vertx.core.Future.succeededFuture("hello $request"))
    }
}