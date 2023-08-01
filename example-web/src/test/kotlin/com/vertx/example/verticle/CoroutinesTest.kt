package com.vertx.example.verticle

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class CoroutinesTest {
    @Test
    fun test(vertx: Vertx, testContext: VertxTestContext) {
        val channel = Channel<Int>(capacity = 3)

        CoroutineScope(vertx.dispatcher()).launch {
            repeat(10) {
                channel.send(it)
                delay(500)
            }
            channel.close()
            testContext.completeNow()
        }
        CoroutineScope(vertx.dispatcher()).launch {
            repeat(10) {
                channel.send(it)
                delay(100)
            }
            channel.close()
            testContext.completeNow()
        }
        CoroutineScope(vertx.dispatcher()).launch {
            for (item in channel) {
                println(item)
            }
        }
        testContext.awaitCompletion(10, java.util.concurrent.TimeUnit.SECONDS)
    }
}