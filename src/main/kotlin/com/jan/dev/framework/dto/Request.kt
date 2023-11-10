package com.jan.dev.framework.dto

import com.jan.dev.framework.annotation.HttpMethod

data class Request(
    val protocol: String? = null,
    val protocolVersion: String? = null,
    val headers: Map<String, String>? = null,
    val body: String? = null,
    val method: HttpMethod? = null,
    val path: String? = null
)
