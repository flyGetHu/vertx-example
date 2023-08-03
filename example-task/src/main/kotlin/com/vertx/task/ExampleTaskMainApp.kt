package com.vertx.task

import cn.hutool.log.StaticLog
import com.hazelcast.config.Config
import com.vertx.task.verticle.MainVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager

object ExampleTaskMainApp {

    @JvmStatic
    fun main(args: Array<String>) {
        val config = Config()
        //关闭组播
        val networkConfig = config.networkConfig
        // 只发现本机成员,防止跨网段广播
        networkConfig.join.multicastConfig.isEnabled = false
        networkConfig.join.tcpIpConfig.isEnabled = true
        networkConfig.join.tcpIpConfig.addMember("127.0.0.1")
        val mgr: ClusterManager = HazelcastClusterManager(config)
        val vertxOptions = VertxOptions()
        vertxOptions.setPreferNativeTransport(true)
        vertxOptions.setClusterManager(mgr)
        Vertx.clusteredVertx(vertxOptions).onSuccess {
            // 关闭jooq的logo
            System.getProperties().setProperty("org.jooq.no-logo", "true")
            System.setProperty("org.jooq.no-tips", "true")
            it.deployVerticle(MainVerticle::class.java.name)
            StaticLog.info("集群启动成功:${it.isClustered}")
        }.onFailure {
            StaticLog.error(it, "集群启动失败")
        }
    }
}