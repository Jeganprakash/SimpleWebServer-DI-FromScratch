package com.jan.dev.webapp.service

import com.jan.dev.framework.annotation.SingletonObject

@SingletonObject
class HealthService {
    fun printHelloWorld() {
        println("hello world ")
    }
}
