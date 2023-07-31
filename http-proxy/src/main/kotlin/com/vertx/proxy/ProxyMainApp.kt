package com.vertx.proxy

import com.vertx.proxy.verticle.MainVerticle
import io.vertx.core.Vertx

object ProxyMainApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val vertx = Vertx.vertx()
        vertx.deployVerticle(MainVerticle::class.java.name)
    }
}