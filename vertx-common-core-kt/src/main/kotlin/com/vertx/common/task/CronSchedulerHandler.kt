package com.vertx.common.task

import cn.hutool.core.date.DateUtil
import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.log.StaticLog
import com.vertx.common.config.active
import com.vertx.common.config.vertx
import com.vertx.common.entity.task.TaskOptions
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
     * @return 返回值为定时任务执行结果,如果不为null,则代表定时任务执行失败,会打印错误日志
     */
    suspend fun task(): String?

    /**
     * 启动定时任务
     * Starts the scheduled task.
     * @param taskOptions 定时任务配置
     *  1: 是否初始化启动 (whether to initialize the task on startup)
     *  2: 启动环境 (startup environment) 如果不为空,则只有在active为startEnv时才会启动定时任务
     *  3: 定义任务执行完成回调函数
     *    入参String,如果不为null,则代表定时任务执行失败,会打印错误日志
     *    此参数可以作为任务执行结果的回调,比如发送邮件通知或者保存到数据库
     */
    fun start(taskOptions: TaskOptions) {
        val startEnv = taskOptions.startEnv
        if (startEnv != null) {
            if (active != startEnv.env) {
                StaticLog.info("当前环境为${active},不启动定时任务:${description}")
                return
            }
        }
        //如果是初始化启动,则直接执行任务
        //If it is an initialization start, execute the task directly.
        if (taskOptions.initStart) {
            CoroutineScope(vertx.dispatcher()).launch {
                try {
                    val msg = task()
                    taskOptions.taskCallback?.let { it(msg, description) }
                } catch (e: Throwable) {
                    StaticLog.error(e, "定时任务执行异常:${description}")
                    taskOptions.taskCallback?.let { it(ExceptionUtil.stacktraceToString(e), description) }
                }
                taskOptions.initStart = false
                start(taskOptions)
            }
            return
        }
        //如果不是初始化启动,则计算下次执行时间,防止初始化执行的同时,也执行了定时任务
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
                    val msg = task()
                    taskOptions.taskCallback?.let { it(msg, description) }
                } catch (e: Throwable) {
                    StaticLog.error(e, "定时任务执行异常:${description}")
                    taskOptions.taskCallback?.let { it(ExceptionUtil.stacktraceToString(e), description) }
                }
                start(taskOptions)
            }
        }
    }

    /**
     * 演示使用,正常情况不需要传递vertx
     * For demonstration purposes only. In normal use, vertx should not be passed as a parameter.
     */
    fun start(taskOptions: TaskOptions, vertx: Vertx = com.vertx.common.config.vertx) {
        val startEnv = taskOptions.startEnv
        if (startEnv != null) {
            if (active != startEnv.env) {
                StaticLog.info("当前环境为${active},不启动定时任务:${description}")
                return
            }
        }
        if (taskOptions.initStart) {
            CoroutineScope(vertx.dispatcher()).launch {
                try {
                    val msg = task()
                    taskOptions.taskCallback?.let { it(msg, description) }
                } catch (e: Throwable) {
                    StaticLog.error(e, "定时任务执行异常:${description}")
                    taskOptions.taskCallback?.let { it(ExceptionUtil.stacktraceToString(e), description) }
                }
                taskOptions.initStart = false
                start(taskOptions, vertx = vertx)
            }
            return
        }
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
                    val msg = task()
                    taskOptions.taskCallback?.let { it(msg, description) }
                } catch (e: Throwable) {
                    StaticLog.error(e, "定时任务执行异常:${description}")
                    taskOptions.taskCallback?.let { it(ExceptionUtil.stacktraceToString(e), description) }
                }
                start(taskOptions, vertx = vertx)
            }
        }
    }
}
