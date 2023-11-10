package com.jan.dev.framework.service.implementations.requestProcessors

import com.jan.dev.framework.annotation.SingletonObject
import com.jan.dev.framework.dto.Request
import com.jan.dev.framework.dto.Response
import com.jan.dev.framework.service.contracts.RequestProcessor

@SingletonObject
class DefaultRequestProcessor : RequestProcessor {

    override fun isSupportedProtocol(request: Request): Boolean {
        return false
    }

    override fun processRequest(request: Request): Response {
        return Response(
            protocol = request.protocol,
            protocolVersion = request.protocolVersion,
            status = 200,
            body = "Hi, I am Jan Server"

        )
    }

    override fun isRequestPathMatched(request: Request): Boolean {
        return true
    }
}
