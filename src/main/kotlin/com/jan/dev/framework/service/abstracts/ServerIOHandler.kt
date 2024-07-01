package com.jan.dev.framework.service.abstracts

import com.jan.dev.framework.dto.WebSocketFrame
import java.io.InputStream
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.util.*

abstract class ServerIOHandler : ServerConnectionThreadHandler() {

    override fun handleConnection(connection: Any) {
        val clientSocket = connection as Socket
        val dataFromClient = readInputStreamAsString(clientSocket.getInputStream())
        val outputData = processInputData(dataFromClient)
        clientSocket.getOutputStream().write(outputData.second.toByteArray())
        if (outputData.first == "websocket") {
            handleWebsocket(clientSocket)
        }
        clientSocket.close()
    }

    private fun handleWebsocket(clientSocket: Socket) {
        val inputStream = clientSocket.getInputStream()

        while (true) {
            val inBuffer = ByteArray(inputStream.available())
            val read = inputStream.read(inBuffer)
            val message = mutableListOf<String>()

            if (inBuffer.isNotEmpty()) {
                val webSocketFrame = parseWebSocketFrame(inBuffer).payloadData
                // TODO need to figure out way to span websocket in new thread
                println(String(webSocketFrame))
            }
        }
    }

    override fun listenPort(portNumber: Int) {
        val serverSocket = ServerSocket(portNumber)
        while (true) {
            val clientSocket = serverSocket.accept()
            spanConnectionInNewThread {
                println("accepted")
                handleConnection(clientSocket)
            }
        }
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
        return message
    }

    abstract fun processInputData(data: List<String>): Pair<String, String>

    private fun parseWebSocketFrame(input: ByteArray): WebSocketFrame {
        // Read the first 2 bytes for the basic frame header
        val buffer = ByteBuffer.wrap(input)

        // Read the first byte
        val firstByte = buffer.get()
        val fin = (firstByte.toInt() and 0x80) != 0
        val rsv1 = (firstByte.toInt() and 0x40) != 0
        val rsv2 = (firstByte.toInt() and 0x20) != 0
        val rsv3 = (firstByte.toInt() and 0x10) != 0
        val opcode = firstByte.toInt() and 0x0F

        // Read the second byte
        val secondByte = buffer.get()
        val masked = (secondByte.toInt() and 0x80) != 0
        var payloadLength = (secondByte.toInt() and 0x7F).toLong()

        // Read extended payload length if needed
        if (payloadLength == 126L) {
            payloadLength = buffer.short.toLong() and 0xFFFF
        } else if (payloadLength == 127L) {
            payloadLength = buffer.long
        }

        // Read the masking key if present
        val maskingKey = if (masked) {
            ByteArray(4).also { buffer.get(it) }
        } else {
            null
        }

        // Read the payload data
        val payloadData = ByteArray(payloadLength.toInt()).also { buffer.get(it) }

        // Unmask the payload data if masked
        if (masked && maskingKey != null) {
            for (i in payloadData.indices) {
                payloadData[i] = (payloadData[i].toInt() xor maskingKey[i % 4].toInt()).toByte()
            }
        }

        return WebSocketFrame(
                fin = fin,
                rsv1 = rsv1,
                rsv2 = rsv2,
                rsv3 = rsv3,
                opcode = opcode,
                masked = masked,
                payloadLength = payloadLength,
                maskingKey = maskingKey,
                payloadData = payloadData
            )
    }
}
