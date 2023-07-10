package com.vertx.example.verticle

import cn.hutool.log.StaticLog
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.kotlin.coroutines.CoroutineVerticle
import java.time.Duration
import java.time.Instant

class MainVerticle : CoroutineVerticle() {
    override fun start(startFuture: Promise<Void>?) {
        val timer = Instant.now()
        Future.all(
            listOf(
                vertx.deployVerticle(EventBusVerticle::class.java.name),
                vertx.deployVerticle(TaskVerticle::class.java.name)
            )
        ).onComplete {
            if (it.succeeded()) {
                StaticLog.info("启动成功:${Duration.between(timer, Instant.now()).toMillis()}ms")
                startFuture?.complete()
            } else {
                StaticLog.error("启动失败:${it.cause()}")
                startFuture?.fail(it.cause())
            }
        }
    }
}