package com.vertx.common.entity.web

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.vertx.common.enums.ApiResponseStatusEnum
import io.vertx.core.json.Json

/**
 * 表示服务器返回的API响应。
 *
 * @property status API 响应的状态。默认值为 ApiResponseStatusEnum.OK。
 * @property code 服务器返回的响应代码。
 * @property msg 服务器返回的响应消息。
 * @property data 响应的数据负载。默认值为空字符串。
 * @property extra 与响应相关的任何额外信息或元数据。默认值为空。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiResponse(
    @JsonProperty("status") val status: ApiResponseStatusEnum = ApiResponseStatusEnum.OK,
    @JsonProperty("code") val code: Int,
    @JsonProperty("msg") val msg: String,
    @JsonProperty("data") var data: Any? = "",
    @JsonProperty("extra") var extra: Any? = ""
) {
    override fun toString(): String {
        return Json.encode(this)
    }
}
