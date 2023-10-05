package com.vertx.webserver.entity

import io.vertx.ext.web.Router

/**
 * WebService configuration class
 */
class WebServiceOptions {
    /**
     * 初始化挂载路由
     */
    var initRouter: (Router) -> Unit = {}
}