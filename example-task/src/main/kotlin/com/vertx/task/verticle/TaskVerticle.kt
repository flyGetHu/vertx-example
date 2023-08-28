package com.vertx.task.verticle

import com.vertx.common.entity.task.TaskOptions
import com.vertx.task.handler.TaskDemoHandlerImpl
import io.vertx.kotlin.coroutines.CoroutineVerticle

class TaskVerticle : CoroutineVerticle() {

    override suspend fun start() {
        try {
            // 启动demo任务
            val taskOptions = TaskOptions()
            taskOptions.initStart = true
            taskOptions.startEnv = null
            TaskDemoHandlerImpl().start(taskOptions = taskOptions)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}