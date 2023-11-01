package com.vertx.task.verticle

import cn.hutool.log.StaticLog
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
            StaticLog.error(e, "启动定时任务失败")
        }
    }
}