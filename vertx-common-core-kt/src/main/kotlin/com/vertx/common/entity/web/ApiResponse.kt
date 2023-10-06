package com.vertx.common.entity.web

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * ApiResponse is a generic data class that represents the response of an API call.
 * @param data
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiResponse(
    @JsonProperty("status") val status: String,
    @JsonProperty("code") val code: Int,
    @JsonProperty("msg") val msg: String,
    @JsonProperty("data") var data: Any? = "",
    @JsonProperty("extra") var extra: Any? = ""
)
