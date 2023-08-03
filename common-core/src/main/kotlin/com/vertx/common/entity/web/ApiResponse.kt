package com.vertx.common.entity.web

/**
 * ApiResponse is a generic data class that represents the response of an API call.
 * @param success indicates whether the API call was successful or not.
 * @param data contains the response data if the API call was successful, null otherwise.
 * @param error contains the error details if the API call was unsuccessful, null otherwise.
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null
)

/**
 * ApiError is a data class that represents the error details of an unsuccessful API call.
 * @param code contains the error code.
 * @param message contains the error message.
 */
data class ApiError(
    val code: Int,
    val message: String
)
