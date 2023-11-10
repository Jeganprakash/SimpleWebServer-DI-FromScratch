package com.jan.dev.framework.service.contracts

import com.jan.dev.framework.dto.Request
import com.jan.dev.framework.dto.Response
import java.io.InputStream
import java.io.OutputStream

interface RequestHandler {
    fun handleRequest(connection: Any)
    fun listenPort(portNumber: Int)
    fun readRequest(inputStream: InputStream): Request
    fun writeResponse(outputStream: OutputStream, response: Response)
    fun <T> spanRequestInNewThread(function: () -> T)
}
