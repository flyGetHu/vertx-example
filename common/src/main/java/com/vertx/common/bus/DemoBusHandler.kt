package com.vertx.common.bus

import com.vertx.common.entity.AppConfig
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

/**
 * 事件总线处理器 测试demo
 * 实现EventBusHandler接口,只需要实现address,确定服务地址,文件命名以BusHandler结尾
 */
open class DemoBusHandler : BusHandler<String, AppConfig> {
    override val requestClass: Class<String> = String::class.java
    override val responseClass: Class<AppConfig> = AppConfig::class.java

    // 事件总线地址
    override val address: String = "demo://eventbus"

    // 在此处不应该去实现具体的逻辑,除非有默认实现,正常情况下应该是抽象方法,在具体的业务模块中重写该方法
    override suspend fun handleRequest(request: String, resultHandler: Handler<AsyncResult<AppConfig>>) {
        // 在此处不应该去实现具体的逻辑,除非有默认实现,正常情况下应该是抽象方法,在具体的业务模块中重写该方法
    }
}