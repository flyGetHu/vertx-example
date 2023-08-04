package com.vertx.example.web.v1.router

import com.vertx.common.config.vertx
import com.vertx.redis.helper.RedisHelper
import com.vertx.redis.helper.RedisLockHelper
import com.vertx.webserver.config.launchCoroutine
import com.vertx.webserver.config.successResponse
import io.vertx.ext.web.Router

/**
 * redis示例路由
 */
object RedisExampleRouter {
    fun init(router: Router) {
        val routerSub = Router.router(vertx)

        routerSub.get("/get/:key").launchCoroutine { ctx ->
            val key = ctx.pathParam("key")
            val value = RedisHelper.Str.get(key)
            ctx.successResponse(value)
        }

        routerSub.post("/set/:key/:value").launchCoroutine { ctx ->
            val key = ctx.pathParam("key")
            val value = ctx.pathParam("value")
            val res = RedisHelper.Str.set(key, value)
            ctx.successResponse(res)
        }

        routerSub.delete("/delete/:key").launchCoroutine { ctx ->
            val key = ctx.pathParam("key")
            val res = RedisHelper.Str.del(key)
            ctx.successResponse(res)
        }

        //测试分布式锁
        routerSub.get("/lock/:key").launchCoroutine { ctx ->
            val key = ctx.pathParam("key")
            val res = RedisLockHelper.lock(key, 10)
            if (res) {
                ctx.successResponse("lock success")
            } else {
                ctx.successResponse("lock fail")
            }
            RedisLockHelper.unlock(key)
        }

        router.route("/redis/*").subRouter(routerSub)
    }
}