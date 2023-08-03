package com.vertx.rabbitmq.handler

import com.vertx.common.entity.mq.MqMessageData
import com.vertx.common.enums.ModelEnum
import com.vertx.rabbitmq.enums.RabbitMqExChangeEnum

/**
 * rabbit队列处理器接口
 * @param Request 请求对象类型
 * @property requestClass 请求对象类型
 */
interface RabbitMqHandler<Request> {
    // The class of the request, e.g. User::class.java
    val requestClass: Class<Request>


    // mq队列交换机枚举,见RabbitMqExChangeEnum
    val exchange: RabbitMqExChangeEnum

    // 模块名称,见ModelEnum
    val moduleName: ModelEnum

    // 队列名称 命名方式:以业务操作命名,见名知意,如:(注册用户)register.user
    // 全小写,单词之间用.分割
    val queueName: String

    // 业务开始日期 命名方式:yyyy-MM-dd,如:2023-08-03
    var date: String

    // 是否持久化,默认true,若为false,则重启服务后队列消失
    var durable: Boolean


    // 是否排他,默认false,若为true,则其他用户无法访问此队列
    var exclusive: Boolean

    // 是否发送确认,默认false,若为true,则发送消息后,需要等待确认
    var confirm: Boolean

    // 最大内部队列大小 默认100 0为不限制,超过此大小,则不再接收消息
    var maxInternalQueueSize: Int


    // 是否自动ack,默认false,若为true,则消费消息后,自动确认
    var autoAck: Boolean

    // 最大重试次数,默认3次,超过此次数,则不再重试
    var maxRetry: Int

    // 重试间隔时间,默认1000毫秒,单位毫秒
    var retryInterval: Long

    /**
     * 消息消费处理器,用于处理消费消息的业务逻辑
     * @param message 消息
     * @return String 空字符串表示消费成功,否则消费失败
     */
    var handler: suspend (Request) -> String?

    /**
     * 消息持久化策略,消息发送前会执行此逻辑,可以将消息保存到数据库或者redis中,用于消息重试
     * @param message 消息
     * @return String 空字符串表示消费成功,否则消费失败
     */
    var persistence: suspend (MqMessageData<Request>) -> String?

    /**
     * 消息消费完成后回调函数,用于通知生产者消息是否消费成功,或者更新消息状态
     * @param  msg 空字符串表示消费成功,否则消费失败
     * @param  msgId 消息id
     * @return String 空字符串表示消费成功,否则消费失败
     */
    var callback: suspend (msg: String, msgId: String) -> Unit
}