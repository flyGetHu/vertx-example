package com.vertx.rabbitmq.helper

import cn.hutool.log.StaticLog
import com.rabbitmq.client.MessageProperties
import com.vertx.common.config.active
import com.vertx.common.config.vertx
import com.vertx.common.entity.MessageData
import com.vertx.rabbitmq.client.rabbitMqClient
import com.vertx.rabbitmq.enums.RabbitMqExChangeEnum
import com.vertx.rabbitmq.enums.RabbitMqExChangeTypeEnum
import com.vertx.rabbitmq.handler.RabbitMqHandler
import io.vertx.core.Promise
import io.vertx.core.json.Json
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.rabbitmq.QueueOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * rabbitmq帮助类
 */
object RabbitMqHelper {

    // 组装队列名称:当前启动环境+模块名称+队列名称+业务开始日期+交换机名称+交换机类型
    private fun assembleQueueName(rabbitMqHandler: RabbitMqHandler<*>): String {
        return "${active}.${rabbitMqHandler.moduleName.name}.${rabbitMqHandler.queueName}.${
            rabbitMqHandler.date.toString(
                "yyyy-MM-dd"
            )
        }.${rabbitMqHandler.exchange.name}.${rabbitMqHandler.exchange.type}"
    }

    // 注册队列
    suspend fun <T> registerQueue(rabbitMqHandler: RabbitMqHandler<T>) {
        // 组装队列名称
        val queueName = assembleQueueName(rabbitMqHandler)
        // 注册队列
        val exchange = rabbitMqHandler.exchange
        // 检查交换机是否为默认交换机
        if (exchange != RabbitMqExChangeEnum.DEFAULT) {
            //注册交换机
            rabbitMqClient.exchangeDeclare(
                exchange.exchanger, exchange.type.name, exchange.durable, exchange.autoDelete
            ).await()
        }
        // 检查交换机消息类型是否匹配
        if (exchange.messageType != rabbitMqHandler.requestClass) {
            StaticLog.error("交换机:${exchange.exchanger}的消息类型不匹配,期望类型:${exchange.messageType},实际类型:${rabbitMqHandler.requestClass}")
            return
        }
        StaticLog.info("注册交换机成功:交换机名称:${exchange.exchanger},交换机类型:${exchange.type.name},是否持久化:${exchange.durable},是否自动删除:${exchange.autoDelete}")
        // 注册队列
        rabbitMqClient.queueDeclare(
            queueName, rabbitMqHandler.durable, rabbitMqHandler.exclusive, rabbitMqHandler.autoAck
        ).await()
        // 绑定队列
        rabbitMqClient.queueBind(queueName, exchange.exchanger, "").await()
        StaticLog.info("注册队列成功:队列名称:$queueName,是否持久化:${rabbitMqHandler.durable},是否排他:${rabbitMqHandler.exclusive},是否自动删除:${rabbitMqHandler.autoAck}")
    }

    /**
     * 发送消息到交换机
     * @param rabbitMqExChangeEnum rabbitmq交换机枚举
     * @param message 消息 与交换机类型匹配
     * @param persistenceMessage 持久化消息函数,如果不需要持久化消息,则传入空函数
     */
    suspend fun sendMessageToExchange(
        rabbitMqExChangeEnum: RabbitMqExChangeEnum,
        message: Any,
        persistenceMessage: suspend (MessageData<Any>) -> Boolean
    ) {
        // 检查交换机类型是否支持发送消息到交换机
        if (rabbitMqExChangeEnum.type == RabbitMqExChangeTypeEnum.DIRECT) {
            throw Exception("direct类型交换机不支持发送消息到交换机")
        }
        // 组装消息
        val messageData = MessageData(message)
        // 持久化消息
        val persistenceRes = persistenceMessage(messageData)
        if (persistenceRes) {
            StaticLog.warn("消息持久化失败:交换机名称:${rabbitMqExChangeEnum.exchanger},消息:$messageData")
        }
        // 检查交换机消息类型是否匹配
        if (rabbitMqExChangeEnum.messageType != message::class.java) {
            throw Exception("消息类型不匹配")
        }
        // 发送消息
        rabbitMqClient.basicPublish(
            rabbitMqExChangeEnum.exchanger, "", MessageProperties.PERSISTENT_TEXT_PLAIN, Json.encodeToBuffer(message)
        ).await()
        StaticLog.info("发送消息成功:队列名称:${rabbitMqExChangeEnum.exchanger},消息:$message")
    }


    // 发送消息到队列
    suspend fun <T> sendMessageToQueue(rabbitMqHandler: RabbitMqHandler<T>, message: T) {
        // 组装队列名称
        val queueName = assembleQueueName(rabbitMqHandler)
        // 组装消息
        val messageData = MessageData(message)
        // 持久化消息
        val persistence = rabbitMqHandler.persistence(messageData)
        if (persistence) {
            StaticLog.warn("消息持久化失败:队列名称:$queueName,消息:$messageData")
        }
        // 发送消息
        rabbitMqClient.basicPublish(
            rabbitMqHandler.exchange.exchanger, queueName, Json.encodeToBuffer(messageData)
        ).compose {
            val promise = Promise.promise<Void>()
            if (rabbitMqHandler.confirm) {
                rabbitMqClient.waitForConfirms(TimeUnit.SECONDS.toMillis(5)).onComplete {
                    if (it.failed()) {
                        promise.fail(it.cause())
                        StaticLog.error("发送消息失败:队列名称:$queueName,消息:$message")
                    } else {
                        promise.complete()
                    }
                }
            } else {
                promise.complete()
            }
            promise.future()
        }.await()
        StaticLog.info("发送消息成功:队列名称:$queueName,消息:$message")
    }

    // 定义消息重试次数map
    private val retryCountMap = ConcurrentHashMap<String, Int>()

    // 注册消费者
    suspend fun <T> registerConsumer(rabbitMqHandler: RabbitMqHandler<T>) {
        // 组装队列名称
        val queueName = assembleQueueName(rabbitMqHandler)
        // 注册消费者
        val queueOptions = QueueOptions()
        queueOptions.setMaxInternalQueueSize(rabbitMqHandler.maxInternalQueueSize)
        //在 RabbitMQ 中，"keepMostRecent" 参数是针对镜像队列（Mirrored Queue）的一种特性。
        // 镜像队列是为了提高消息队列的高可用性而引入的一种机制，它通过在多个节点之间复制队列的消息来确保消息的冗余存储。
        //
        //"keepMostRecent" 参数用于指定在镜像队列中保留的消息的数量。当设置了这个参数时，
        // RabbitMQ 只会保留指定数量的最近消息，而丢弃较旧的消息。这个参数可以在队列创建时进行设置。
        //
        //使用 "keepMostRecent" 参数的优点在于，可以控制队列中的消息数量，防止队列过于堆积，
        // 从而减少队列中消息的存储和处理开销。通常，对于高可用性的需求，你可能会配置多个节点来镜像队列，通过限制保留的消息数量，可以避免在所有节点上保存大量冗余的消息。
        //
        //需要注意的是，镜像队列对于提高高可用性是有帮助的，但在节点之间进行消息同步也会带来一些性能开销。
        // 因此，在设置 "keepMostRecent" 参数时，需要根据实际业务需求和性能要求进行权衡和配置。
        queueOptions.setKeepMostRecent(false)
        val autoAck = rabbitMqHandler.autoAck
        queueOptions.setAutoAck(autoAck)
        val rabbitMQConsumer = rabbitMqClient.basicConsumer(queueName, queueOptions).await()
        val requestClass = rabbitMqHandler.requestClass

        rabbitMQConsumer.handler { rabbitMQMessage ->
            CoroutineScope(vertx.dispatcher()).launch {
                try {
                    val deliveryTag = rabbitMQMessage.envelope().deliveryTag
                    val messageData = Json.decodeValue(rabbitMQMessage.body(), MessageData::class.java)
                    val msgId = messageData.id
                    val message = messageData.msg
                    if (message == null) {
                        StaticLog.error("消息解析失败:队列名称:$queueName,消息:$messageData")
                        ackMessage(autoAck, msgId, deliveryTag)
                        return@launch
                    }
                    if (message::class.java != requestClass) {
                        StaticLog.error("消息类型不匹配:队列名称:$queueName,消息:$messageData")
                        ackMessage(autoAck, messageData.id, deliveryTag)
                        return@launch
                    }
                    val res = rabbitMqHandler.handler(message as T)
                    if (!res) {
                        StaticLog.error("消息处理失败:队列名称:$queueName,消息:$messageData")
                        //判断是否需要重试
                        if (retryCountMap.getOrDefault(messageData.id, 0) >= rabbitMqHandler.maxRetry) {
                            StaticLog.error("消息重试次数超过最大重试次数:队列名称:$queueName,消息:$messageData")
                            ackMessage(autoAck, msgId, deliveryTag)
                            return@launch
                        }
                        // 重回队列
                        nackMessage(deliveryTag, rabbitMqHandler.retryInterval)
                        return@launch
                    }
                    ackMessage(autoAck, msgId, deliveryTag)
                } catch (e: Throwable) {
                    StaticLog.error(e, "消息处理失败:队列名称:$queueName,消息:${rabbitMQMessage.body()}")
                    if (!autoAck) {
                        nackMessage(rabbitMQMessage.envelope().deliveryTag, rabbitMqHandler.retryInterval)
                    }
                }
            }
        }
        StaticLog.info("注册消费者成功:队列名称:$queueName")
    }

    // ack消息
    private fun ackMessage(autoAck: Boolean, msgId: String, deliveryTag: Long) {
        retryCountMap.remove(msgId)
        if (autoAck) {
            return
        }
        // ack消息
        rabbitMqClient.basicAck(deliveryTag, false).onComplete {
            if (it.failed()) {
                StaticLog.error(it.cause(), "ack消息失败:deliveryTag:$deliveryTag")
            }
        }
    }

    // 重回队列
    private fun nackMessage(deliveryTag: Long, retryInterval: Long) {
        // 延迟重回队列
        vertx.setTimer(retryInterval) {
            rabbitMqClient.basicNack(deliveryTag, false, true).onComplete {
                if (it.failed()) {
                    StaticLog.error(it.cause(), "重回队列失败:deliveryTag:$deliveryTag")
                }
            }
        }
    }
}