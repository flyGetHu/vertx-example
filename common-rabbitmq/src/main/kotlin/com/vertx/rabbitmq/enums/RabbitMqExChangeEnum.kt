package com.vertx.rabbitmq.enums

/**
 * rabbitmq交换机枚举
 * @property type 交换机类型
 * @property exchanger 交换机名称
 */
enum class RabbitMqExChangeEnum(
    val type: RabbitMqExChangeTypeEnum,
    val exchanger: String,
    val messageType: Any = String::class.java,
    val durable: Boolean = true,
    val autoDelete: Boolean = false,
) {
    /**
     * 默认交换机
     */
    DEFAULT(RabbitMqExChangeTypeEnum.DEFAULT, ""),

    /**
     * 直连交换机
     */
    DIRECT(RabbitMqExChangeTypeEnum.DIRECT, "direct"),

    /**
     * 主题交换机
     */
    TOPIC(RabbitMqExChangeTypeEnum.TOPIC, "topic"),

    /**
     * 头交换机
     */
    HEADERS(RabbitMqExChangeTypeEnum.HEADERS, "headers"),

    /**
     * 扇形交换机
     */
    FANOUT(RabbitMqExChangeTypeEnum.FANOUT, "fanout");
}