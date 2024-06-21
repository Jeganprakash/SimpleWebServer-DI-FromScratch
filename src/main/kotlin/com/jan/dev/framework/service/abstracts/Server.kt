package com.jan.dev.framework.service.abstracts

interface Server {
    fun handleConnection(connection: Any)
    fun listenPort(portNumber: Int)
    fun <T> spanConnectionInNewThread(function: () -> T)
}
