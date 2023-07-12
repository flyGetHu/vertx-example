package com.vertx.common.bus

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler

object MyBusServiceImpl : BusHandler<String, String> {
    override val requestClass: Class<String> = String::class.java
    override val responseClass: Class<String> = String::class.java
    override val address: String = "test://test"


    override suspend fun handleRequest(request: String, resultHandler: Handler<AsyncResult<String>>) {
        resultHandler.handle(Future.succeededFuture("success $request"))
    }
}