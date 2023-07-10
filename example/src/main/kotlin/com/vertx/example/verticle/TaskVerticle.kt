package com.vertx.example.verticle

import cn.hutool.log.StaticLog
import com.vertx.example.task.TaskDemoHandlerImpl
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * 任务启动类
 */
class TaskVerticle : CoroutineVerticle() {

    override suspend fun start() {
        try {
            StaticLog.info("TaskVerticle启动类开始启动")
            // 启动demo任务
            TaskDemoHandlerImpl().start()
            StaticLog.info("TaskVerticle启动类启动成功")
        } catch (e: Throwable) {
            StaticLog.error(e, "TaskVerticle启动类启动失败")
        }
    }
}