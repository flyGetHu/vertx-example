package com.vertx.common.utils

import cn.hutool.core.date.DateUtil
import cn.hutool.log.StaticLog
import io.vertx.core.impl.logging.LoggerFactory
import org.apache.logging.log4j.core.util.CronExpression
import java.text.ParseException
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

/**
 * Cron表达式工具类
 *
 * @param cronExpressionString cron表达式
 * &#064;Author:  huan
 */
class CronSchedulerUtil(cronExpressionString: String?) {

	/**
	 * 日志
	 */
	private val logger = LoggerFactory.getLogger(this.javaClass)

	/**
	 * cron表达式
	 */
	private var cronExpression: CronExpression? = null

	/**
	 * 下次执行时间
	 */
	private var nextExecutionTime: LocalDateTime? = null

	/**
	 * 构造器 初始化cron表达式
	 */
	init {
		try {
			cronExpression = CronExpression(cronExpressionString)
			logger.info("cron表达式${cronExpressionString}初始化成功")
		} catch (e: ParseException) {
			StaticLog.error("cron表达式格式错误:{}", cronExpressionString)
			throw RuntimeException(e)
		}
		calculateNextExecutionTime()
	}


	/**
	 * 获取下次执行时间 单位:ms
	 *
	 * @return 下次执行时间
	 */
	fun getTimeUntilNextExecution(): Long {
		val now = LocalDateTime.now()
		if (now.isAfter(nextExecutionTime)) {
			calculateNextExecutionTime()
		}
		val duration = Duration.between(now, nextExecutionTime)
		//Cannot schedule a timer with delay < 1 ms
		if (duration.toMillis() < 1) {
			StaticLog.warn("cron表达式{}下次执行时间小于1ms,重新计算", cronExpression!!.cronExpression)
			return getTimeUntilNextExecution()
		}
		return duration.toMillis()
	}

	/**
	 * 计算下次执行时间
	 */
	private fun calculateNextExecutionTime() {
		val now = Date()
		val date = cronExpression!!.getNextValidTimeAfter(now)
		nextExecutionTime = DateUtil.toLocalDateTime(date)
	}
}
