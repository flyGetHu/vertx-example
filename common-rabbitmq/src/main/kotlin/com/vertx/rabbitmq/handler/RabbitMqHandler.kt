package com.vertx.rabbitmq.handler

import cn.hutool.core.date.DateTime
import com.vertx.common.entity.MessageData
import com.vertx.common.enums.ModelEnum
import com.vertx.rabbitmq.enums.RabbitMqExChangeEnum

/**
 * rabbit队列处理器接口
 * @param Request 请求对象类型
 * @param Response 响应对象类型
 * @property requestClass 请求对象类型
 * @property responseClass 响应对象类型
 */
interface RabbitMqHandler<Request> {
    // The class of the request
    val requestClass: Class<Request>


    // mq队列交换机枚举
    val exchange: RabbitMqExChangeEnum

    // 模块名称
    val moduleName: ModelEnum

    // 队列名称 命名方式:以业务操作命名,见名知意,如:(注册用户)register.user
    val queueName: String

    // 业务开始日期
    var date: DateTime

    // 是否持久化
    var durable: Boolean


    // 是否排他
    var exclusive: Boolean

    // 是否发送确认
    var confirm: Boolean

    // 最大内部队列大小
    var maxInternalQueueSize: Int


    // 是否自动ack
    var autoAck: Boolean

    // 最大重试次数
    var maxRetry: Int

    // 重试间隔时间
    var retryInterval: Long

    // 是否延迟队列
    var delay: Boolean

    // 延迟时间
    var delayTime: Long

    /**
     * 消息消费处理器
     * @param message 消息
     * @return Boolean 是否处理成功
     */
    suspend fun handler(message: Request): Boolean {
        return true
    }

    /**
     * 消息持久化策略
     * @param message 消息
     * @return Boolean 是否持久化成功
     */
    suspend fun persistence(message: MessageData<Request>): Boolean {
        return true
    }
}