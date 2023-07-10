package com.vertx.common.task

import cn.hutool.core.date.DateUtil
import cn.hutool.log.StaticLog
import com.vertx.common.utils.CronSchedulerUtil
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 定时任务接口
 */
interface CronSchedulerHandler {

    /**
     * 任务描述
     */
    var description: String

    /**
     * 定时任务表达式
     */
    var scheduler: CronSchedulerUtil

    /**
     * 定时任务
     */
    suspend fun task()

    /**
     * 启动定时任务
     * @param initStart 是否初始化启动
     */
    fun start(initStart: Boolean = false) {
        //如果是初始化启动,则直接执行任务
        if (initStart) {
            CoroutineScope(Vertx.currentContext().owner().dispatcher()).launch {
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
        if (!initStart) {
            val timeUntilNextExecution = scheduler.getTimeUntilNextExecution()
            StaticLog.info(
                "定时任务:${description} 下次执行时间:${
                    DateUtil.offsetMillisecond(
                        DateUtil.date(),
                        timeUntilNextExecution.toInt()
                    )
                }"
            )
            val vertx = Vertx.currentContext().owner()
            vertx.setTimer(timeUntilNextExecution) { event ->
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
                        DateUtil.date(),
                        timeUntilNextExecution.toInt()
                    )
                }"
            )
            vertx.setTimer(timeUntilNextExecution) { event ->
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
