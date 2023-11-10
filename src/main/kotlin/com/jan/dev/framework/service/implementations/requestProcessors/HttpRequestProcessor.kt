package com.jan.dev.framework.service.implementations.requestProcessors

import com.jan.dev.framework.annotation.HttpMethod
import com.jan.dev.framework.annotation.Injected
import com.jan.dev.framework.annotation.SingletonObject
import com.jan.dev.framework.dto.Request
import com.jan.dev.framework.dto.Response
import com.jan.dev.framework.service.contracts.RequestProcessor
import com.jan.dev.framework.util.AnnotationUtil
import java.lang.reflect.Method

@SingletonObject
class HttpRequestProcessor : RequestProcessor {

    @Injected
    private lateinit var annotationUtil: AnnotationUtil

    override fun isSupportedProtocol(request: Request): Boolean {
        return request.protocol.equals("HTTP", true)
    }

    override fun processRequest(request: Request): Response {
        val (obj, method) = getPathMatchedObjectMethod(request.path!!, request.method!!)!!
        return method.invoke(obj, request) as Response
    }

    override fun isRequestPathMatched(request: Request): Boolean {
        getPathMatchedObjectMethod(request.path!!, request.method!!)?.let { return true }
        return false
    }

    private fun getPathMatchedObjectMethod(path: String, httpMethod: HttpMethod): Pair<Any, Method>? {
        val obj = annotationUtil.getPathMatchedControllerAnnotatedObject(path, httpMethod)
            ?: return null
        val method = annotationUtil.getPathMatchedMethod(obj, path, httpMethod)
        return Pair(obj, method)
    }
}
