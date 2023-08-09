/**
 * This file contains the VertxLoadConfig object which is responsible for loading the configuration file and initializing the Vertx global object, event bus, shared data container, and log object.
 * The active variable is used to specify the environment variable to be used. The appConfig variable holds the loaded configuration.
 * The init() function is a suspend function that initializes the Vertx global object, event bus, shared data container, and log object.
 * It loads the configuration file based on the active variable and initializes the WebClient object with the loaded configuration.
 */
package com.vertx.common.config

import cn.hutool.log.LogFactory
import cn.hutool.log.StaticLog
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory
import com.vertx.common.entity.app.AppConfig
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.core.shareddata.SharedData
import io.vertx.kotlin.coroutines.await


// 是否已经初始化
var isInit = false

// 环境变量 如需要使用自定义的环境变量,修改此处即可 默认config.dev.yaml
var active = "dev"

// 配置文件
lateinit var appConfig: AppConfig

// 配置文件json对象 此对象保存配置文件中所有信息,若appConfig对象中没有,则从此对象中获取
lateinit var appConfigJson: JsonObject

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

object VertxLoadConfig {

    /**
     * 加载配置文件
     * @param active 环境变量,默认dev,如果需要使用自定义的环境变量,调用此方法前修改此参数即可
     * 如此参数为test,则会加载config.test.yaml配置文件
     * @see appConfig
     */
    suspend fun init(active: String = "dev") {
        vertx = Vertx.currentContext().owner()
        eventBus = vertx.eventBus()
        sharedData = vertx.sharedData()
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
        val devExists = vertx.fileSystem().exists(activeConfigName).await()
        if (!devExists) {
            StaticLog.warn("当前项目激活配置环境文件:$activeConfigName 不存在,请检查配置文件是否需要加载配置文件!")
            return
        }
        com.vertx.common.config.active = env
        StaticLog.info("当前项目激活配置环境文件:$activeConfigName")
        val configRetrieverOptions = ConfigRetrieverOptions()
        configRetrieverOptions.addStore(
            ConfigStoreOptions().setType("file").setFormat("yaml").setConfig(JsonObject().put("path", activeConfigName))
        )
        val params = ConfigRetriever.create(vertx, configRetrieverOptions).config.await()
        //若appConfig中不存在对应的配置属性,可以使用此属性获取
        appConfigJson = params
        val config = params.mapTo(AppConfig::class.java)
        appConfig = config
        isInit = true
    }
}