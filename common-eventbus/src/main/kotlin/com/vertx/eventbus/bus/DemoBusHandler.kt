/**
 * This class is a demo implementation of the BusHandler interface for handling events on the event bus.
 * It specifies the request and response classes and the event bus address.
 * The handleRequest method is left unimplemented and should be overridden in the specific business module.
 * @param requestClass The class of the request object.
 * @param responseClass The class of the response object.
 * @param address The address on the event bus where this handler will listen for events.
 */
package com.vertx.eventbus.bus

import com.vertx.common.model.User
import com.vertx.eventbus.handler.BusHandler
import io.vertx.core.Future

/**
 * 事件总线处理器 测试demo
 * 实现EventBusHandler接口,只需要实现address,确定服务地址,文件命名以BusHandler结尾
 */
open class DemoBusHandler : BusHandler<String, List<User>> {
    override val requestClass: Class<String> = String::class.java
    override val responseClass: Class<List<User>> = List::class.java as Class<List<User>>

    // 事件总线地址
    override val address: String = "demo://eventbus"

    // 在此处不应该去实现具体的逻辑,除非有默认实现,正常情况下应该是抽象方法,在具体的业务模块中重写该方法
    override suspend fun handleRequest(request: String): Future<List<User>> {
        // 在此处不应该去实现具体的逻辑,除非有默认实现,正常情况下应该是抽象方法,在具体的业务模块中重写该方法
        return Future.succeededFuture()
    }
}