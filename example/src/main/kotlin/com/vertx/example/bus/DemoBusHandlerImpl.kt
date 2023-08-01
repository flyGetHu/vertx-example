package com.vertx.example.bus

import cn.hutool.log.StaticLog
import com.vertx.common.config.appConfig
import com.vertx.common.entity.AppConfig
import com.vertx.eventbus.bus.DemoBusHandler
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

object DemoBusHandlerImpl : DemoBusHandler() {

    override suspend fun handleRequest(request: String, resultHandler: Handler<AsyncResult<AppConfig>>) {
        StaticLog.info("DemoBusHandlerImpl: $request")
        resultHandler.handle(io.vertx.core.Future.succeededFuture(appConfig))
    }
}