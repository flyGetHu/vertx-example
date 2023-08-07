package com.vertx.rabbitmq

import cn.hutool.log.StaticLog
import com.vertx.rabbitmq.verticle.MainVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.spi.cluster.hazelcast.ConfigUtil
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager


object ExampleRabbitMqMainApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val hazelcastConfig = ConfigUtil.loadConfig()
        //关闭组播
        val networkConfig = hazelcastConfig.networkConfig
        // 只发现本机成员,防止跨网段广播
        networkConfig.join.multicastConfig.isEnabled = false
        networkConfig.join.tcpIpConfig.isEnabled = true
        networkConfig.join.tcpIpConfig.addMember("127.0.0.1")
        val mgr: ClusterManager = HazelcastClusterManager(hazelcastConfig)
        val vertxOptions = VertxOptions()
        vertxOptions.setClusterManager(mgr)
        Vertx.clusteredVertx(vertxOptions).onSuccess {
            // 关闭jooq的logo
            System.setProperty("org.jooq.no-logo", "true")
            System.setProperty("org.jooq.no-tips", "true")
            it.deployVerticle(MainVerticle::class.java.name)
            StaticLog.info("集群启动成功:${it.isClustered}")
        }.onFailure {
            StaticLog.error(it, "集群启动失败")
        }
    }
}