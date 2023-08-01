package com.vertx.example.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.vertx.common.config.TableName

/**
 * -- auto-generated definition
 * create table user
 * (
 *     id          int auto_increment
 *         primary key,
 *     name        varchar(255) not null,
 *     age         int          not null,
 *     create_time timestamp    not null,
 *     update_time timestamp    not null
 * );
 *
 */
// JsonIgnoreProperties 忽略未知属性
// TableName 表名
// 时间格式的字段需要使用String类型接收,懒得处理了麻烦
@JsonIgnoreProperties(ignoreUnknown = true)
@TableName("user")
data class User(
    @JsonProperty("id") val id: Int?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("age") val age: Int?,
    @JsonProperty("create_time") val createTime: String?,
    @JsonProperty("update_time") val updateTime: String?
)