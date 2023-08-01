package com.vertx.task.verticle

import com.vertx.task.handler.TaskDemoHandlerImpl
import io.vertx.kotlin.coroutines.CoroutineVerticle

class TaskVerticle : CoroutineVerticle() {

    override suspend fun start() {
        try {
            // 启动demo任务
            TaskDemoHandlerImpl().start(vertx)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}