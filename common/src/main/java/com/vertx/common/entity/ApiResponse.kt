package com.vertx.common.entity

import com.vertx.common.config.jsonResponse
import io.vertx.ext.web.RoutingContext

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null
)

data class ApiError(
    val code: Int,
    val message: String
)

