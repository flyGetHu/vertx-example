package com.vertx.example

import cn.hutool.log.StaticLog
import com.hazelcast.config.Config
import com.vertx.example.verticle.MainVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager

object ExampleMainApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val config = Config()
        //关闭组播
        val networkConfig = config.networkConfig
        networkConfig.join.multicastConfig.isEnabled = false
        networkConfig.join.tcpIpConfig.isEnabled = true
        networkConfig.join.tcpIpConfig.addMember("127.0.0.1")
        val mgr: ClusterManager = HazelcastClusterManager(config)
        val vertxOptions = VertxOptions()
        vertxOptions.setClusterManager(mgr)
        Vertx.clusteredVertx(vertxOptions).onComplete { res ->
            if (res.succeeded()) {
                val vertx = res.result()
                vertx.deployVerticle(MainVerticle::class.java.name)
            } else {
                StaticLog.error(res.cause(), "启动失败")
            }
        }
    }
}