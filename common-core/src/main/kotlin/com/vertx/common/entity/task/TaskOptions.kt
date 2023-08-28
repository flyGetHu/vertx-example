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
}