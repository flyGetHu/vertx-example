package com.vertx.example.verticle

import cn.hutool.log.StaticLog
import com.hazelcast.config.Config
import com.vertx.common.bus.DemoBusHandler
import com.vertx.common.config.VertxLoadConfig
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class BusVerticleTest {


    @RepeatedTest(3)
    fun test(testContext: VertxTestContext) {
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
        Vertx.clusteredVertx(vertxOptions).onComplete { result ->
            assertTrue(result.succeeded())
            val vertx = result.result()
            val sharedData = vertx.sharedData()
            CoroutineScope(vertx.dispatcher()).launch {
                VertxLoadConfig.init()
                //测试计数器
                val counter = sharedData.getCounter("counter").await()
                vertx.setPeriodic(100) { _ ->
                    CoroutineScope(vertx.dispatcher()).launch {
                        val count = counter.decrementAndGet().await()
                        if (count < 10) {
                            testContext.completeNow()
                        }
                        val appConfig = DemoBusHandler().call("vertx").await()
                        StaticLog.info("appConfig:{}", appConfig)
                    }
                }
            }
        }
        testContext.awaitCompletion(100, java.util.concurrent.TimeUnit.SECONDS)
    }
}