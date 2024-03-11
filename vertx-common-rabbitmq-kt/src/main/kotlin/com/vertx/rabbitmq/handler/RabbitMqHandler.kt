/**
 * This interface defines the basic information of a RabbitMQ queue.
 *
 * @param Request the type of the request object
 * @property requestClass the class of the request object
 * @property exchange the exchange of the RabbitMQ queue
 * @property moduleName the module name of the RabbitMQ queue
 * @property queueName the name of the RabbitMQ queue
 * @property date the start date of the business, in the format yyyy-MM-dd
 * @property durable whether the queue is durable or not, default is true
 * @property exclusive whether the queue is exclusive or not, default is false
 * @property confirm whether to wait for confirmation after sending a message, default is false
 * @property maxInternalQueueSize the maximum size of the internal queue, default is 100
 * @property autoAck whether to automatically acknowledge the message after consuming it, default is false
 * @property maxRetry the maximum number of retries, default is 3
 * @property retryInterval the interval between retries, in milliseconds, default is 1000
 * @property handler the consumer handler, which handles the business logic of consuming messages
 * @property persistence the persistence handler, which handles the persistence logic of messages before sending them
 * @property callback the callback handler, which handles the callback logic after sending messages
 */
package com.vertx.rabbitmq.handler

import com.vertx.common.entity.mq.MqMessageData
import com.vertx.common.enums.IModelEnum
import com.vertx.rabbitmq.enums.IRabbitMqExChangeEnum

/**
 * rabbit队列处理器接口
 * 此接口用于定义rabbitmq队列的基本信息
 *
 * @param Request 请求对象类型
 * @property handler 消费处理器,具体处理业务逻辑,接收到消息后,会调用此处理器
 * @property persistence 持久化处理器,在发送消息前,会调用此处理器,将消息持久化到数据库,如果不需要持久化,则空函数即可
 * @property callback 回调处理器,在发送消息后,会调用此处理器,将消息发送结果回调给调用方,如果不需要回调,则空函数即可
 */
interface RabbitMqHandler<Request> {
    // 队列消息实体类型
    val requestClass: Class<Request>


    // mq队列交换机枚举,见RabbitMqExChangeEnum
    val exchange: IRabbitMqExChangeEnum

    // 模块名称,见ModelEnum
    val moduleName: IModelEnum

    // 队列名称 命名方式:以业务操作命名,见名知意,如:(注册用户)register.user
    // 全小写,单词之间用.分割
    val queueName: String

    // 业务开始日期 命名方式:yyyy-MM-dd,如:2023-08-03
    var date: String

    /**
     * 是否持久化,默认true,若为false,则重启服务后队列消失
     * durable 属性：当队列被声明为持久化（durable）时，意味着该队列将在 RabbitMQ 服务器重启后仍然保留。
     * 持久化队列的消息会被保存在磁盘上，以确保消息不会因服务器重启而丢失。
     * exclusive 属性：当队列被声明为独占（exclusive）时，意味着该队列只能被声明它的连接所访问。连接断开时，
     * 队列会被自动删除。独占队列通常用于实现连接级别的私有队列，例如在消费者和生产者之间进行通信。
     * 冲突情况：
     * 如果队列同时被声明为 durable 和 exclusive，这可能会导致冲突。因为 exclusive 队列通常用于在连接级别提供私有性，
     * 这意味着只有声明该队列的连接可以访问它。但是，如果队列是持久化的，那么即使连接断开，队列也会被保留，这可能与 exclusive 的预期行为相冲突。
     * 另一方面，如果队列被声明为 non-durable（即不持久化）和 exclusive，这种情况通常是允许的。这是因为即使队列是独占的，
     * 它不会在连接断开时保留，而且由于不持久化，队列和队列中的消息都不会存储在磁盘上。
     */
    var durable: Boolean


    // 是否排他,默认false,若为true,则其他用户无法访问此队列
    // 配置exclusive为true,就算配置durable也为true,也会在重启后消失
    // 要想持久化,则需要配置durable为true,exclusive为false
    var exclusive: Boolean

    // 最大内部队列大小 默认100 0为不限制,超过此大小,则不再接收消息
    var maxInternalQueueSize: Int


    // 是否自动ack,默认false,若为true,则消费消息后,自动确认
    var autoAck: Boolean

    // 最大重试次数,默认3次,超过此次数,则不再重试
    var maxRetry: Int

    // 重试间隔时间,默认1000毫秒,单位毫秒
    var retryInterval: Long

    /**
     * 消费方
     * 消息消费处理器,用于处理消费消息的业务逻辑
     * @param message 消息
     * @return String 空字符串表示消费成功,否则消费失败
     */
    suspend fun handler(message: Request): String?

    /**
     * 发送方
     * 消息持久化策略,消息发送前会执行此逻辑,可以将消息保存到数据库或者redis中,用于消息重试
     * 此函数主要由发送方实现,如果不需要持久化,则空函数即可
     * @param message 消息
     * @return String 空字符串表示消费成功,否则消费失败
     */
    suspend fun persistence(message: MqMessageData<Request>): String?

    /**
     * 消费方
     * 消息消费完成后回调函数,用于通知生产者消息是否消费成功,或者更新消息状态
     * 此函数主要由消费方实现,如果不需要回调,则空函数即可
     * @param  msg 空字符串表示消费成功,否则消费失败
     * @param  msgId 消息id
     * @return String 空字符串表示消费成功,否则消费失败
     */
    suspend fun callback(msg: String, msgId: String)
}