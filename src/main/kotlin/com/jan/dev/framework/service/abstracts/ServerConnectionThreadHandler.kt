package com.jan.dev.framework.service.abstracts

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class ServerConnectionThreadHandler : Server {
    private val numThreads = 20
    private val executorService: ExecutorService = Executors.newFixedThreadPool(numThreads)

    override fun <T> spanConnectionInNewThread(function: () -> T) {
        executorService.submit(function)
    }
}
