package com.jan.dev.framework.dto

data class Response(
    val protocol: String? = null,
    val protocolVersion: String? = null,
    val headers: Map<String, String>? = null,
    val body: String? = null,
    val status: Int? = null
) {
    fun getResponseAsString(): String {
        val responseStringBuilder = StringBuilder()

        // Append protocol and protocol version if available
        if (protocol != null && protocolVersion != null) {
            responseStringBuilder.append("$protocol/$protocolVersion ")
        }

        // Append status code if available
        if (status != null) {
            responseStringBuilder.append("$status\n")
        }

        // Append headers if available
        headers?.forEach { (key, value) ->
            responseStringBuilder.append("$key: $value\n")
        }

        // Append body if available
        if (body != null) {
            responseStringBuilder.append("\n$body")
        }

        // Append next line
        responseStringBuilder.append("\n")

        return responseStringBuilder.toString()
    }
}
