package com.vertx.common.task

import cn.hutool.core.date.DateUtil
import cn.hutool.log.StaticLog
import com.vertx.common.config.vertx
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 定时任务接口
 * Interface for scheduling tasks at specific intervals using cron expressions.
 */
interface CronSchedulerHandler {

    /**
     * 任务描述
     * Description of the task.
     */
    var description: String

    /**
     * 定时任务表达式
     * Cron expression for scheduling the task.
     */
    var scheduler: CronScheduler

    /**
     * 定时任务
     * Coroutine function that defines the task to be executed.
     */
    suspend fun task()

    /**
     * 启动定时任务
     * Starts the scheduled task.
     * @param initStart 是否初始化启动 (whether to initialize the task on startup)
     */
    fun start(initStart: Boolean = false) {
        //如果是初始化启动,则直接执行任务
        //If it is an initialization start, execute the task directly.
        if (initStart) {
            CoroutineScope(vertx.dispatcher()).launch {
                try {
                    task()
                } catch (e: Throwable) {
                    StaticLog.error(e, "定时任务执行异常:${description}")
                }
                start(false)
            }
            return
        }
        //如果不是初始化启动,则计算下次执行时间,防止初始化执行的同时,也执行了定时任务
        //If it is not an initialization start, calculate the next execution time to prevent the task from being executed while initializing.
        if (!initStart) {
            val timeUntilNextExecution = scheduler.getTimeUntilNextExecution()
            StaticLog.info(
                "定时任务:${description} 下次执行时间:${
                    DateUtil.offsetMillisecond(
                        DateUtil.date(), timeUntilNextExecution.toInt()
                    )
                }"
            )
            vertx.setTimer(timeUntilNextExecution) {
                CoroutineScope(vertx.dispatcher()).launch {
                    try {
                        task()
                    } catch (e: Throwable) {
                        StaticLog.error(e, "定时任务执行异常:${description}")
                    }
                    start(false)
                }
            }
        }
    }

    /**
     * 演示使用,正常情况不需要传递vertx
     * For demonstration purposes only. In normal use, vertx should not be passed as a parameter.
     */
    fun start(vertx: Vertx, initStart: Boolean = false) {
        if (initStart) {
            CoroutineScope(vertx.dispatcher()).launch {
                try {
                    task()
                } catch (e: Throwable) {
                    StaticLog.error(e, "定时任务执行异常:${description}")
                }
                start(vertx = vertx, initStart = false)
            }
            return
        }
        if (!initStart) {
            val timeUntilNextExecution = scheduler.getTimeUntilNextExecution()
            StaticLog.info(
                "定时任务:${description} 下次执行时间:${
                    DateUtil.offsetMillisecond(
                        DateUtil.date(), timeUntilNextExecution.toInt()
                    )
                }"
            )
            vertx.setTimer(timeUntilNextExecution) {
                CoroutineScope(vertx.dispatcher()).launch {
                    try {
                        task()
                    } catch (e: Throwable) {
                        StaticLog.error(e, "定时任务执行异常:${description}")
                    }
                    start(vertx = vertx, initStart = false)
                }
            }
        }
    }
}
