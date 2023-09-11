package com.vertx.example.bus

import cn.hutool.log.StaticLog
import com.vertx.common.enums.SharedCounterEnum
import com.vertx.common.helper.SharedCounterHelper
import com.vertx.common.model.User
import com.vertx.eventbus.bus.DemoBusHandler
import com.vertx.example.mapper.UserMapper
import io.vertx.core.Future

/**
 * 事件总线服务提供方实现类
 * 1: 先在本目录下创建一个服务实现类,实现EventBusHandler接口,只需要实现address,确定服务地址,文件命名以BusHandler结尾
 * 2: 然后在具体的业务模块实现rpc接口的逻辑handleRequest,文件BusHandlerImpl结尾
 * 3: 其他模块调用该服务,只需要调用本目录下对应实现的call方法即可
 * @constructor 创建一个事件总线服务提供方实现类
 */
object DemoBusHandlerImpl : DemoBusHandler() {

    /**
     * BUS处理逻辑
     * @param request 请求参数
     * @return Future<Response> 响应结果
     */
    override suspend fun handleRequest(request: String): Future<List<User>> {
        StaticLog.info("DemoBusHandlerImpl: $request")
        val users = UserMapper.list(2)
        val counterNum = SharedCounterHelper.getCounterNum(SharedCounterEnum.TEST_SHARED_COUNTER)
        StaticLog.info("DemoBusHandlerImpl: $counterNum")
        return Future.succeededFuture(users)
    }
}