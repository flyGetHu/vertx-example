package com.vertx.common.entity.task

import com.vertx.common.enums.EnvEnum

/**
 * Task configuration class
 */
class TaskOptions {

    /**
     * 是否初始化启动 (whether to initialize the task on startup)
     */
    var initStart: Boolean = false

    /**
     * 启动环境 (startup environment) 如果不为空,则只有在active为startEnv时才会启动定时任务
     */
    var startEnv: EnvEnum? = null

    /**
     * 定义任务执行完成回调函数
     * 入参String,如果不为null,则代表定时任务执行失败,会打印错误日志
     * 此参数可以作为任务执行结果的回调,比如发送邮件通知或者保存到数据库
     */
    var taskCallback: (suspend (String?, String) -> Unit)? = null
}