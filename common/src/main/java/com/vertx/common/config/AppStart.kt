package com.vertx.common.config

import cn.hutool.log.StaticLog
import com.hazelcast.config.Config
import com.vertx.common.entity.AppConfig
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.kotlin.core.json.get
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager

/**
 * 应用启动
 * @Author huan
 */
fun appStart() {
    val config = Config()
    //关闭组播
    val networkConfig = config.networkConfig
    // 只发现本机成员,防止跨网段广播
    networkConfig.join.multicastConfig.isEnabled = false
    networkConfig.join.tcpIpConfig.isEnabled = true
    networkConfig.join.tcpIpConfig.addMember("127.0.0.1")
    val mgr: ClusterManager = HazelcastClusterManager(config)
    val vertxOptions = VertxOptions()
    vertxOptions.setClusterManager(mgr)
    val startVerticleContext = JsonObject()
    Vertx.clusteredVertx(vertxOptions).compose {
        startVerticleContext.put("vertx", it);
        loadConfig(it)
    }.compose {
        startVerticleContext.put("appConfig", it);
        Future.succeededFuture<Void>()
    }.compose {
        val vertx = startVerticleContext.get<Vertx>("vertx")
        val appConfig = startVerticleContext.get<AppConfig>("appConfig")
        val vertxConfig = appConfig.vertx
        val deploymentOptions = DeploymentOptions()
        deploymentOptions.isHa = vertxConfig.ha
        deploymentOptions.instances = vertxConfig.instances
        val verticle = vertxConfig.verticle
        vertx.deployVerticle(verticle, deploymentOptions)
    }.onComplete { res ->
        if (res.succeeded()) {
            StaticLog.info("启动成功")
        } else {
            StaticLog.error(res.cause(), "启动失败")
        }
    }
}