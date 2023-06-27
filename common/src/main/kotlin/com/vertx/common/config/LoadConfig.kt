package com.vertx.common.config

import cn.hutool.log.StaticLog
import com.vertx.common.entity.AppConfig
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await


// 环境变量
var active = "dev"

// 配置文件
lateinit var appConfig: AppConfig

/**
 * 加载配置文件
 */
object LoadConfig {
    suspend fun loadConfig(): AppConfig {
        val vertx = Vertx.currentContext().owner()
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
        configRetrieverOptions.addStore(ConfigStoreOptions().setType("file").setFormat("yaml").setConfig(JsonObject().put("path", activeConfigName)))
        val params = ConfigRetriever.create(vertx, configRetrieverOptions).config.await()
        val config = params.mapTo(AppConfig::class.java)
        appConfig = config
        return config
    }
}