package com.vertx.common.entity

import cn.hutool.core.lang.UUID

/**
 * 消息数据
 * @param T 消息类型
 * @property id 消息id
 * @property msg 消息
 */
class MessageData<T>(// 消息
    var msg: T
) {
    // 消息id
    var id = UUID.fastUUID().toString()

}