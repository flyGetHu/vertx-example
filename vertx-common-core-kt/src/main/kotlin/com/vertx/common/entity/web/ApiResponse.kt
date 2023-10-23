package com.vertx.common.entity.web

import cn.hutool.http.HttpStatus
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
    @JsonProperty("status") var status: ApiResponseStatusEnum = ApiResponseStatusEnum.OK,
    @JsonProperty("code") var code: Int,
    @JsonProperty("msg") var msg: String,
    @JsonProperty("data") var data: Any? = "",
    @JsonProperty("extra") var extra: Any? = ""
) {
    override fun toString(): String {
        return Json.encode(this)
    }
}

/**
 * 使用给定的数据和额外信息将响应设置为成功状态。
 *
 * @param data 要包含在响应中的数据。默认为空字符串。
 * @param extra 响应中包含的额外信息。默认为空字符串。
 * @return 更新后的 ApiResponse 对象，包含成功状态以及提供的数据和额外信息。
 */
fun successResponse(data: Any? = "", extra: Any? = ""): ApiResponse {
    return ApiResponse(
        status = ApiResponseStatusEnum.OK,
        code = HttpStatus.HTTP_OK,
        msg = "",
        data = data,
        extra = extra
    )
}

/**
 * 更新 ApiResponse 对象的属性以表示错误响应。
 *
 * @param msg 要设置的错误消息。
 * @param code 要设置的错误代码。默认值为 HttpStatus.HTTP_INTERNAL_ERROR。
 * @param data 与错误响应关联的附加数据。默认值为空字符串。
 * @param extra 与错误响应相关的附加信息。默认值为空字符串。
 * @return 代表错误响应的更新后的 ApiResponse 对象。
 */
fun errorResponse(
    msg: String,
    code: Int = HttpStatus.HTTP_INTERNAL_ERROR,
    data: Any? = "",
    extra: Any? = ""
): ApiResponse {
    return ApiResponse(status = ApiResponseStatusEnum.ERROR, code = code, msg = msg, data = data, extra = extra)
}