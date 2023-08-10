package com.vertx.example.web.v1.router

import cn.hutool.core.util.RandomUtil
import cn.hutool.log.StaticLog
import com.vertx.breaker.handler.BreakerHandler
import com.vertx.common.config.vertx
import com.vertx.webserver.helper.launchCoroutine
import com.vertx.webserver.helper.successResponse
import io.vertx.ext.web.Router
import kotlinx.coroutines.delay

object BreakerRouter {

    fun init(router: Router) {
        val routerSub = Router.router(vertx)

        routerSub.get("/test").launchCoroutine {
            val res = BreakerHandler.execute(
                name = "breaker-test-1",
                timeout = 500,
                maxRetries = 2,
                maxFailures = 5,
                resetTimeout = 500,
                failuresRollingWindow = 60000,
                metricsRollingWindow = 60000,
                action = {
                    StaticLog.info("action")
                    val randomInt = RandomUtil.randomLong(300, 1000)
                    delay(randomInt)
                    "action"
                },
                fallback = {
                    StaticLog.warn("fallback")
                    "fallback"
                },
            )
            it.successResponse(res)
        }

        router.route("/breaker/*").subRouter(routerSub)
    }
}