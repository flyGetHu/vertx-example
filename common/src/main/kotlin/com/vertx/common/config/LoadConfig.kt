package com.vertx.common.config

import cn.hutool.log.LogFactory
import cn.hutool.log.StaticLog
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory
import com.vertx.common.entity.AppConfig
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.core.shareddata.SharedData
import io.vertx.kotlin.coroutines.await


// 环境变量
var active = "dev"

// 配置文件
lateinit var appConfig: AppConfig

// vertx全局对象
var vertx: Vertx = Vertx.currentContext().owner()

// 事件总线
val eventBus: EventBus = vertx.eventBus()

/**
 * vertx 共享数据容器
 * synchronous maps (local-only)
 * asynchronous maps
 * asynchronous locks
 * asynchronous counters
 */
var sharedData: SharedData = vertx.sharedData()

/**
 * 加载配置文件
 */
object LoadConfig {
    suspend fun loadConfig(): AppConfig {
        StaticLog.info("项目是否为集群环境:${vertx.isClustered}")
        //配置默认的log日志对象
        val logFactory = Log4j2LogFactory.create()
        LogFactory.setCurrentLogFactory(logFactory)
        StaticLog.info("初始化日志对象成功:" + logFactory.name)
        val retriever = ConfigRetriever.create(vertx)
        val jsonObject = retriever.config.await()
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
        val params = ConfigRetriever.create(vertx, configRetrieverOptions).config.await()
        val config = params.mapTo(AppConfig::class.java)
        appConfig = config
        return config
    }
}