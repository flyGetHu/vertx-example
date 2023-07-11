package com.vertx.common.config

import cn.hutool.log.LogFactory
import cn.hutool.log.StaticLog
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory
import com.vertx.common.entity.AppConfig
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.core.shareddata.SharedData


// 环境变量 如需要使用自定义的环境变量,修改此处即可 默认config.dev.yaml
var active = "dev"

// 配置文件
lateinit var appConfig: AppConfig

// vertx全局对象
lateinit var vertx: Vertx

// 事件总线
lateinit var eventBus: EventBus

/**
 * vertx 共享数据容器
 * synchronous maps (local-only)
 * asynchronous maps
 * asynchronous locks
 * asynchronous counters
 */
lateinit var sharedData: SharedData

/**
 * 加载配置文件
 */
fun loadConfig(vertx: Vertx): Future<AppConfig> {
    val promise = Promise.promise<AppConfig>()
    com.vertx.common.config.vertx = vertx
    eventBus = vertx.eventBus()
    sharedData = vertx.sharedData()
    //配置默认的log日志对象
    val logFactory = Log4j2LogFactory.create()
    LogFactory.setCurrentLogFactory(logFactory)
    StaticLog.info("初始化日志对象成功:" + logFactory.name)
    val retriever = ConfigRetriever.create(vertx)
    retriever.config.compose { jsonObject ->
        var activeConfigName = "conf/config."
        var env = active
        if (jsonObject.containsKey("active")) {
            env = jsonObject.getString("active")
        }
        activeConfigName += "$env.yaml"
        StaticLog.info("当前项目激活配置环境文件:$activeConfigName")
        val configRetrieverOptions = ConfigRetrieverOptions()
        configRetrieverOptions.addStore(
            ConfigStoreOptions().setType("file").setFormat("yaml").setConfig(JsonObject().put("path", activeConfigName))
        )
        ConfigRetriever.create(vertx, configRetrieverOptions).config
    }.onComplete {
        if (it.succeeded()) {
            val params = it.result()
            val config = params.mapTo(AppConfig::class.java)
            appConfig = config
            promise.complete(config)

        } else {
            StaticLog.error("加载配置文件失败:${it.cause()}")
            promise.fail(it.cause())
        }
    }
    return promise.future()
}
