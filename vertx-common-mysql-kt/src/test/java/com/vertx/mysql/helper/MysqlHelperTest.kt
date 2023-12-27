package com.vertx.mysql.helper

import cn.hutool.core.date.DateUtil
import cn.hutool.log.StaticLog
import com.vertx.common.config.VertxLoadConfig
import com.vertx.common.config.appConfig
import com.vertx.common.model.User
import com.vertx.common.utils.underlineName
import com.vertx.mysql.client.MysqlClient
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jooq.Condition
import org.jooq.impl.DSL
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(VertxExtension::class)
class MysqlHelperTest {
    val vertx = Vertx.vertx()

    @BeforeEach
    fun deployVerticle(vertx: Vertx, testContext: VertxTestContext) {
        CoroutineScope(vertx.dispatcher()).launch {
            VertxLoadConfig.init()
            MysqlClient.init(appConfig.database?.mysql!!)
            testContext.completeNow()
        }
    }

    @Test
    fun testInsert() {
        val user = User(12, "2", 12, "222", "222")
        try {
            user.javaClass.getDeclaredField("sss")
        } catch (_: NoSuchFieldException) {
            StaticLog.warn("对象${user::class.java.name}不存在id主键")
            0
        }
    }

    @Test
    fun testWithTransaction(testContext: VertxTestContext) {
        CoroutineScope(vertx.dispatcher()).launch {
            val result = MysqlHelper.withTransaction { sqlConnection ->
                MysqlHelper.insert(User(12, "2", 12, DateUtil.now(), DateUtil.now()), sqlConnection)
                println(1 / 0)
                MysqlHelper.delete(User::class.java, DSL.field(User::name.name.underlineName()).eq("2"), sqlConnection)
                val rows = sqlConnection.query("select * from user").execute().await()
                rows.map {
                    Json.decodeValue(it.toJson().toBuffer(), User::class.java)
                }
            }
            println(result)
            testContext.completeNow()
        }
    }

    @Test
    fun testUpdateBatch(testContext: VertxTestContext) {
        CoroutineScope(vertx.dispatcher()).launch {
            MysqlHelper.withTransaction {
                val dataList = mutableListOf<Map<Any, Condition>>()
                for (i in 0..100) {
                    val user = User(null, "2", 12, null, null)
                    val condition = DSL.field(User::id.name.underlineName()).eq(i)
                    dataList.add(mutableMapOf(Pair(user, condition)))
                }
                MysqlHelper.updateBatch(dataList)
            }
            testContext.completeNow()
        }
    }

    @Test
    suspend fun testTransaction(testContext: VertxTestContext) {
        val str = "select * from user";
        val withTransaction = MysqlHelper.withTransaction {
            it.query(str).execute().await()
            it.query(str).execute().await()
            it.query(str).execute().await()
            it.query(str).execute().await()
            it.query(str).execute().await()
        }
        testContext.completeNow()
    }
}