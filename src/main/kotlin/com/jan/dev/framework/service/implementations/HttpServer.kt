package com.jan.dev.framework.service.implementations

import com.jan.dev.framework.annotation.HttpMethod
import com.jan.dev.framework.annotation.Injected
import com.jan.dev.framework.annotation.SingletonObject
import com.jan.dev.framework.dto.Request
import com.jan.dev.framework.dto.Response
import com.jan.dev.framework.service.abstracts.ServerIOHandler
import com.jan.dev.framework.util.SingletonUtil
import java.lang.Exception
import java.lang.reflect.Method
import java.security.MessageDigest
import java.util.*

@SingletonObject
class HttpServer : ServerIOHandler() {

    @Injected
    private lateinit var annotationUtil: SingletonUtil

    override fun processInputData(data: List<String>): Pair<String, String> {
        val request = parseHttpRequestText(data)
        val (obj, method) = getRequestPathMatchedController(request.path!!, request.method!!) ?: throw Exception("404 not found")
        if (isWebsocketUpgradeRequest(request)) {
            return Pair("websocket", switchToWebsocket(request).getResponseAsString())
        }
        val response = method.invoke(obj, request) as Response
        return Pair("HTTP", response.getResponseAsString())
    }

    private fun getRequestPathMatchedController(path: String, httpMethod: HttpMethod): Pair<Any, Method>? {
        val obj = annotationUtil.getPathMatchedController(path, httpMethod)
            ?: return null
        val method = annotationUtil.getPathMatchedMethod(obj, path, httpMethod)
        return Pair(obj, method)
    }

    private fun parseHttpRequestText(requestData: List<String>): Request {
        if (requestData.isEmpty()) return Request()

        val firstLine = requestData[0].split(" ")
        val httpMethod = firstLine[0]
        val path = firstLine[1]
        val protocolParts = firstLine[2].split("/")
        val protocol = protocolParts[0]
        val protocolVersion = protocolParts[1]

        val headers = mutableMapOf<String, String>()
        var body: String? = null
        var currentLineCount = 1
        var line = requestData[currentLineCount]

        // read headers from request data
        while (line.isNotBlank()) {
            val headerParts = line.split(":")
            headers[headerParts[0]] = headerParts[1].trim()
            currentLineCount += 1
            if (currentLineCount > requestData.size) break
            line = requestData[currentLineCount]
        }

        // read body from request data
        currentLineCount += 1
        if (currentLineCount <= requestData.size) {
            val bodyTexts = requestData.subList(currentLineCount, requestData.size)
            body = bodyTexts.joinToString(separator = "")
        }

        return Request(
            method = HttpMethod.valueOf(httpMethod),
            protocol = protocol,
            protocolVersion = protocolVersion,
            path = path,
            headers = headers,
            body = body
        )
    }

    private fun isWebsocketUpgradeRequest(request: Request): Boolean {
        return request.method == HttpMethod.GET &&
                request.headers?.get("Connection")?.contains("Upgrade") == true &&
                request.headers?.get("Upgrade") == "websocket"
    }

    private fun switchToWebsocket(request: Request): Response {
        // Validate websocket request
        if (request.method != HttpMethod.GET ||
            request.headers?.get("Connection")?.contains("Upgrade") != true ||
            request.headers["Upgrade"] != "websocket" ||
            request.headers["Sec-WebSocket-Key"] == null ||
            request.headers["Sec-WebSocket-Version"] != "13") {

            // Return error response if validation fails
            return Response(
                protocol = request.protocol,
                protocolVersion = request.protocolVersion,
                headers = mapOf("Content-Type" to "text/plain"),
                body = "Invalid WebSocket request",
                status = 400 // Bad Request
            )
        }

        // Generate Sec-WebSocket-Accept header value
        val webSocketKey = request.headers["Sec-WebSocket-Key"] ?: ""
        val acceptKey = Base64.getEncoder().encodeToString(
            MessageDigest.getInstance("SHA-1").digest((webSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").toByteArray())
        )

        // Return 101 Switching Protocols response
        return Response(
            protocol = request.protocol,
            protocolVersion = request.protocolVersion,
            headers = mapOf(
                "Upgrade" to "websocket",
                "Connection" to "Upgrade",
                "Sec-WebSocket-Accept" to acceptKey
            ),
            status = 101 // Switching Protocols
        )
    }
}
