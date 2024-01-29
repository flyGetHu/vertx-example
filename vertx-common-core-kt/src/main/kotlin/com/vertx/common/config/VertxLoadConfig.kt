/**
 * This file contains the VertxLoadConfig object which is responsible for loading the configuration file and initializing the Vertx global object, event bus, shared data container, and log object.
 * The active variable is used to specify the environment variable to be used. The appConfig variable holds the loaded configuration.
 * The init() function is a suspend function that initializes the Vertx global object, event bus, shared data container, and log object.
 * It loads the configuration file based on the active variable and initializes the WebClient object with the loaded configuration.
 */
package com.vertx.common.config

import cn.hutool.core.util.ClassUtil
import cn.hutool.log.LogFactory
import cn.hutool.log.StaticLog
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory
import com.vertx.common.annotations.TableName
import com.vertx.common.annotations.UniqueAddress
import com.vertx.common.entity.app.AppConfig
import com.vertx.common.exception.UniqueAddressException
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

// 环境变量 此环境变量不允许改动
var active = ""

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
        // 防止接口定义地址重复
        localClassFilter()
        vertx = Vertx.currentContext().owner()
        eventBus = vertx.eventBus()
        sharedData = vertx.sharedData()
        StaticLog.info("项目是否为集群环境:${vertx.isClustered}")
        //配置默认的log日志对象
        val logFactory = Log4j2LogFactory.create()
        LogFactory.setCurrentLogFactory(logFactory)
        StaticLog.info("初始化日志对象成功:" + logFactory.name)
//        val retriever = ConfigRetriever.create(vertx)
//        val jsonObject = retriever.config.await()
//        StaticLog.info("获取环境变量成功:\n${jsonObject.encodePrettily()}")
        var activeConfigName = "conf/config."
        var env = active
        // 如果com.vertx.common.config.active不是空则按照此值来初始化
        if (com.vertx.common.config.active.isNotBlank()) {
            env = com.vertx.common.config.active
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
        StaticLog.info("加载配置文件成功:\n${params.encodePrettily()}")
        //若appConfig中不存在对应的配置属性,可以使用此属性获取
        appConfigJson = params
        val config = params.mapTo(AppConfig::class.java)
        appConfig = config
        isInit = true
    }

    private fun localClassFilter() {
        val classes: Set<Class<*>> = ClassUtil.scanPackage()
        // 用于存储事件总线的唯一地址
        val eventBusUniqueAddress: MutableSet<String> = HashSet()
        for (aClass in classes) {
            //判断类是否有指定注解
            if (aClass.isAnnotationPresent(UniqueAddress::class.java)) {
                //获取注解对象
                val uniqueAddress = aClass.getAnnotation(UniqueAddress::class.java)
                //获取注解的值
                val uniqueAddressVal = uniqueAddress.value
                if (eventBusUniqueAddress.contains(uniqueAddressVal)) {
                    // 打印警告信息
                    StaticLog.warn("注意EventBus接口和RabbitMq接口:")
                    StaticLog.warn("接口定义类命名不带impl,并且类上必须添加UniqueAddress注解,标明地址路径")
                    StaticLog.warn("项目启动会检查是否有重复的地址或者地址缺失")
                    StaticLog.warn("最终实现消费或者服务提供方命名必须添加impl,检查会过滤末尾Impl文件")
                    // 抛出唯一地址异常，提示类名和重复的地址
                    StaticLog.error("${aClass.name}\n地址重复:$uniqueAddress")
                    throw UniqueAddressException("${aClass.name}\n地址重复:$uniqueAddress")
                }
                // 将唯一地址添加到事件总书唯一地址集合中
                eventBusUniqueAddress.add(uniqueAddressVal)
            }
            if (aClass.isAnnotationPresent(TableName::class.java)) {
                // 检查数据库模型类是否有id字段
                val idField = "id"
                val field = ClassUtil.getDeclaredField(aClass, idField)
                if (field == null) {
                    StaticLog.warn("警告: 数据库模型类${aClass.name}没有名为\"$idField\"的字段,请检查是否有误")
                }
            }
        }
    }
}