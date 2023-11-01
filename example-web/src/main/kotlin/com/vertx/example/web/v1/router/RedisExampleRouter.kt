package com.vertx.example.web.v1.router

import cn.hutool.log.StaticLog
import com.vertx.common.config.vertx
import com.vertx.common.enums.SharedLockSharedLockEnum
import com.vertx.common.helper.SharedLockHelper
import com.vertx.redis.helper.RedisHelper
import com.vertx.webserver.helper.errorResponse
import com.vertx.webserver.helper.launchCoroutine
import com.vertx.webserver.helper.successResponse
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
        routerSub.get("/lock/test").launchCoroutine { ctx ->
            try {
                val lock =
                    SharedLockHelper.getLocalLockWithTimeout(SharedLockSharedLockEnum.TEST_SHARED_LOCK, 10 * 1000)
                try {
                    ctx.successResponse("lock success")
                } finally {
                    lock.release()
                }
            } catch (e: Exception) {
                StaticLog.error(e, "lock fail")
                ctx.errorResponse(message = "lock fail")
            }
        }

        router.route("/redis/*").subRouter(routerSub)
    }
}