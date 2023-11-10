package com.jan.dev.framework.factory

import com.jan.dev.framework.annotation.Injected
import com.jan.dev.framework.annotation.SingletonObject
import com.jan.dev.framework.dto.Request
import com.jan.dev.framework.service.contracts.RequestProcessor
import com.jan.dev.framework.service.implementations.requestProcessors.DefaultRequestProcessor

@SingletonObject
class RequestProcessorFactory {

    @Injected
    private lateinit var requestProcessors: List<RequestProcessor>
    @Injected
    private lateinit var defaultRequestProcessor: DefaultRequestProcessor // TODO::Implement default request processor

    fun getRequestProcessor(request: Request): RequestProcessor {
        requestProcessors.forEach { processor ->
            if (processor.isSupportedProtocol(request) && processor.isRequestPathMatched(request))
                return processor
        }
        return defaultRequestProcessor
    }
}
