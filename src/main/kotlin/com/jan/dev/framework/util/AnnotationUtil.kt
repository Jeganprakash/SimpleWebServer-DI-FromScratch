package com.jan.dev.framework.util

import com.jan.dev.framework.annotation.Controller
import com.jan.dev.framework.annotation.HttpMethod
import com.jan.dev.framework.annotation.RequestPath
import com.jan.dev.framework.annotation.SingletonObject
import com.jan.dev.framework.factory.SingletonObjectsFactory
import java.lang.reflect.Method

@SingletonObject
class AnnotationUtil {
    fun getPathMatchedControllerAnnotatedObject(path: String, method: HttpMethod): Any? {
        val objects = getAllControllerAnnotatedObjects()
        objects.forEach {
            val controllerPathsList = getAllControllerPathsOfObject(it, method)
            if (controllerPathsList.contains(path)) return it
        }
        return null
    }

    private fun getAllControllerAnnotatedObjects(): List<Any> {
        return SingletonObjectsFactory.getObjects(Controller::class.java).toList()
    }

    private fun getAllControllerPathsOfObject(obj: Any, method: HttpMethod): List<String> {
        val controllerAnnotation = obj.javaClass.getAnnotation(Controller::class.java) ?: return emptyList()
        val basePath = controllerAnnotation.path
        return obj.javaClass.methods
            .filter { it.isAnnotationPresent(RequestPath::class.java) && it.getAnnotation(RequestPath::class.java).method == method }
            .map { basePath + it.getAnnotation(RequestPath::class.java).path }
    }

    fun getPathMatchedMethod(obj: Any, path: String, method: HttpMethod): Method {
        val controllerAnnotation = obj.javaClass.getAnnotation(Controller::class.java)
        val basePath = controllerAnnotation.path
        return obj.javaClass.methods
            .filter { it.isAnnotationPresent(RequestPath::class.java) && it.getAnnotation(RequestPath::class.java).method == method }
            .first { (basePath + it.getAnnotation(RequestPath::class.java).path) == path }
    }
}
