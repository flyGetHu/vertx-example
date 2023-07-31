package com.vertx.example.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.vertx.common.config.TableName

/**
 * create table user
 * (
 *     id          int          not null,
 *     name        varchar(255) not null,
 *     age         int          not null,
 *     create_time timestamp    not null,
 *     update_time timestamp    not null
 * );
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@TableName("user")
data class User(
    @JsonProperty("id") val id: Int?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("age") val age: Int?,
    @JsonProperty("create_time") val createTime: String?,
    @JsonProperty("update_time") val updateTime: String?
)