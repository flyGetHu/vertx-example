package com.vertx.common.config

import com.vertx.common.entity.web.ApiResponse
import com.vertx.common.enums.ApiResponseStatusEnum
import io.vertx.core.json.Json
import org.junit.jupiter.api.Test

class LoadConfigTest {
    @Test
    fun testLoadConfig() {
        val apiResponse = ApiResponse(ApiResponseStatusEnum.ERROR, 400, "error", "data", "extra")
        println(apiResponse)
        println(Json.encode(apiResponse))
    }
}