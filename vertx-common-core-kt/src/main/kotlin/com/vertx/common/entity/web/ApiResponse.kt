package com.vertx.common.entity.web

/**
 * ApiResponse is a generic data class that represents the response of an API call.
 * @param data
 */
data class ApiResponse(
    val code: Int, val msg: String, var data: Any? = "", var extra: Any? = ""
)
