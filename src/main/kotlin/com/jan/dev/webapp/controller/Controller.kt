package com.jan.dev.webapp.controller

import com.jan.dev.framework.annotation.Controller
import com.jan.dev.framework.annotation.HttpMethod
import com.jan.dev.framework.annotation.Injected
import com.jan.dev.framework.annotation.RequestPath
import com.jan.dev.framework.annotation.SingletonObject
import com.jan.dev.framework.dto.Request
import com.jan.dev.framework.dto.Response
import com.jan.dev.webapp.service.HealthService
import com.jan.dev.webapp.service.ThreadDetailsService

@Controller
@SingletonObject
class Controller {

    @Injected
    private lateinit var healthService: HealthService

    @Injected
    private lateinit var threadDetailsService: ThreadDetailsService

    @RequestPath(method = HttpMethod.GET, path = "health")
    fun getHealth(request: Request): Response {
        healthService.printHelloWorld()
        val body = "health is fantastic"
        return Response(
            status = 200,
            protocol = request.protocol,
            protocolVersion = request.protocolVersion,
            body = body,
            headers = request.headers)
    }

    @RequestPath(method = HttpMethod.GET, path = "")
    fun getThreadDetails(request: Request): Response {
        val body = threadDetailsService.getCurrentThreadDetails()
        return Response(
            status = 200,
            protocol = request.protocol,
            protocolVersion = request.protocolVersion,
            body = body,
            headers = request.headers)
    }
}
