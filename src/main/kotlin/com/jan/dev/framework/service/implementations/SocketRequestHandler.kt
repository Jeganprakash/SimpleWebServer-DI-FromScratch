package com.jan.dev.framework.service.implementations

import com.jan.dev.framework.annotation.HttpMethod
import com.jan.dev.framework.annotation.Injected
import com.jan.dev.framework.annotation.SingletonObject
import com.jan.dev.framework.dto.Request
import com.jan.dev.framework.dto.Response
import com.jan.dev.framework.factory.RequestProcessorFactory
import com.jan.dev.framework.service.abstracts.ThreadPoolRequestHandler
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*

@SingletonObject
class SocketRequestHandler : ThreadPoolRequestHandler() {

    @Injected
    private lateinit var requestProcessorFactory: RequestProcessorFactory

    override fun handleRequest(connection: Any) {
        val clientSocket = connection as Socket
        val request = readRequest(clientSocket.getInputStream())
        val requestProcessor = requestProcessorFactory.getRequestProcessor(request) // TODO::Handle Null pointer exception
        val response = requestProcessor.processRequest(request)
        writeResponse(clientSocket.getOutputStream(), response)
        clientSocket.close()
    }

    override fun listenPort(portNumber: Int) {
        val serverSocket = ServerSocket(portNumber)
        while (true) {
            val clientSocket = serverSocket.accept()
            spanRequestInNewThread {
                println("accepted")
                handleRequest(clientSocket)
            }
        }
    }

    override fun writeResponse(outputStream: OutputStream, response: Response) {
        val responseString = response.getResponseAsString()
        outputStream.write(responseString.toByteArray())
    }

    override fun readRequest(inputStream: InputStream): Request {
        val requestData = readInputStreamAsString(inputStream)
        return parseHttpRequestText(requestData)
    }
    private fun readInputStreamAsString(inputStream: InputStream): List<String> {
        val inBuffer = CharArray(inputStream.available())
        val inReader = InputStreamReader(inputStream)
        val read = inReader.read(inBuffer)
        val message = mutableListOf<String>()

        Scanner(String(inBuffer)).use { sc ->
            while (sc.hasNextLine()) {
                val line = sc.nextLine()
                message.add(line)
            }
        }
        println(message)
        return message
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
            headers[headerParts[0]] = headerParts[1]
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
}
