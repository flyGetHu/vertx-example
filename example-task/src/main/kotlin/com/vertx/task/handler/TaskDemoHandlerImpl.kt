package com.vertx.task.handler

import cn.hutool.core.date.DateUtil
import cn.hutool.log.StaticLog
import com.vertx.common.task.CronScheduler
import com.vertx.common.task.CronSchedulerHandler

/**
 * 测试定时任务
 */
class TaskDemoHandlerImpl : CronSchedulerHandler {

    // 任务描述
    override var description: String = "测试定时任务"

    // 每5秒执行一次
    override var scheduler: CronScheduler = CronScheduler("0/5 * * * * ?")

    // 任务执行体
    override suspend fun task(): String? {
        StaticLog.info("${DateUtil.date()}:任务执行成功")
        return null
    }
}