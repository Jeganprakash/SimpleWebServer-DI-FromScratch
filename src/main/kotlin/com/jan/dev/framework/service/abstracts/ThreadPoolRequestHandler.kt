package com.jan.dev.framework.service.abstracts

import com.jan.dev.framework.service.contracts.RequestHandler
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class ThreadPoolRequestHandler : RequestHandler {
    private val numThreads = 20
    private val executorService: ExecutorService = Executors.newFixedThreadPool(numThreads)

    override fun <T> spanRequestInNewThread(function: () -> T) {
        executorService.submit(function)
    }
}
