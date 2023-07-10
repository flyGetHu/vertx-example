package com.vertx.common.bus

import io.vertx.core.AsyncResult
import io.vertx.core.Handler

/**
 * 事件总线处理器 测试demo
 * 实现EventBusHandler接口,只需要实现address,确定服务地址,文件命名以BusHandler结尾
 */
open class DemoBusHandler : BusHandler<String, String> {
    // 事件总线地址
    override val address: String = "demo://eventbus"

    // 在此处不应该去实现具体的逻辑,除非有默认实现,正常情况下应该是抽象方法,在具体的业务模块中重写该方法
    override suspend fun handleRequest(request: String, resultHandler: Handler<AsyncResult<String>>) {
        TODO("Not yet implemented")
    }
}