package com.jan.dev.framework.service.contracts

import com.jan.dev.framework.dto.Request
import com.jan.dev.framework.dto.Response

interface RequestProcessor {
    fun isSupportedProtocol(request: Request): Boolean
    fun processRequest(request: Request): Response
    fun isRequestPathMatched(request: Request): Boolean
}
