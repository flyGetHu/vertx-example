package com.vertx.common.entity.mq

import cn.hutool.core.lang.UUID
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 消息数据
 * @param T 消息类型
 * @property id 消息id
 * @property body 消息
 */
class MqMessageData<T>(// 消息
    @JsonProperty("body") var body: T
) {
    // 消息id
    @JsonProperty("id")
    var id = UUID.fastUUID().toString()
}