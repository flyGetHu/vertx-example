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
import com.vertx.common.exception.UniqueAddressException
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
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
        checkUniqueAddress()
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

    /**
     * 检查对应的消息总线,mq队列等接口实现上是否有地址重复问题
     */
    private fun checkUniqueAddress() {
        val scanResult = ClassGraph().enableAllInfo()
            .enableStaticFinalFieldConstantInitializerValues()
            .scan()
        // 检查事件总线接口是否有地址重复
        scanAndValidateHandlers(scanResult, "com.vertx.eventbus.handler.BusHandler")
        // 检查rabbitmq接口是否有地址重复
        scanAndValidateHandlers(scanResult, "com.vertx.rabbitmq.handler.RabbitMqHandler")
        // 检查数据库模型类是否有id字段
        scanResult.getClassesWithAnnotation("com.vertx.common.annotations.TableName").forEach {
            val idField = "id"
            if (it.getFieldInfo().stream().noneMatch { fieldInfo -> fieldInfo.name.equals(idField) }) {
                StaticLog.warn("数据库模型类${it.name}没有名为\"$idField\"的字段,请检查是否有误")
            }
        }
    }

    /**
     * 扫描并验证指定包中的事件总线处理程序类。
     *
     * @param scanResult 扫描操作的结果。
     * @param packageName 要扫描的包的名称。
     * @throws Exception 如果存在验证错误，例如事件总线地址重复或丢失。
     */
    private fun scanAndValidateHandlers(scanResult: ScanResult, packageName: String) {
        val busHandlerClasses = scanResult.getClassesImplementing(packageName)
        val eventBusUniqueAddress = mutableSetOf<String>()
        for (classInfo in busHandlerClasses) {
            val className = classInfo.name
            if (className.endsWith("Impl")) {
                continue
            }
            // 获取类address属性注释
            val annotationInfos = classInfo.annotationInfo
            var hasUniqueAddress = false
            for (annotationInfo in annotationInfos) {
                if (annotationInfo.name == "com.vertx.common.annotations.UniqueAddress") {
                    val uniqueAddress = annotationInfo.parameterValues[0].value as String
                    if (eventBusUniqueAddress.contains(uniqueAddress)) {
                        //打印警告信息:接口定义类命名不带impl,最终实现消费或者服务提供方命名必须添加impl
                        StaticLog.warn("注意EventBus接口和RabbitMq接口:")
                        StaticLog.warn("接口定义类命名不带impl,并且类上必须添加UniqueAddress注解,标明地址路径")
                        StaticLog.warn("项目启动会检查是否有重复的地址或者地址缺失")
                        StaticLog.warn("最终实现消费或者服务提供方命名必须添加impl,检查会过滤末尾Impl文件")
                        throw UniqueAddressException("$className\n地址重复:$uniqueAddress")
                    }
                    eventBusUniqueAddress.add(uniqueAddress)
                    hasUniqueAddress = true
                }
            }
            if (!hasUniqueAddress) {
                //打印警告信息:接口定义类命名不带impl,最终实现消费或者服务提供方命名必须添加impl
                StaticLog.warn("注意EventBus接口和RabbitMq接口:")
                StaticLog.warn("接口定义类命名不带impl,并且类上必须添加UniqueAddress注解,标明地址路径")
                StaticLog.warn("项目启动会检查是否有重复的地址或者地址缺失")
                StaticLog.warn("最终实现消费或者服务提供方命名必须添加impl,检查会过滤末尾Impl文件")
                throw UniqueAddressException("$className\n事件总线地址未设置")
            }
        }
    }
}