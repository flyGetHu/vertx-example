package com.vertx

import com.vertx.verticle.MainVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager

object MainApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val mgr: ClusterManager = HazelcastClusterManager()
        val vertxOptions = VertxOptions()
        vertxOptions.setClusterManager(mgr)
        Vertx.clusteredVertx(vertxOptions)
        Vertx.vertx().deployVerticle(MainVerticle)
    }
}