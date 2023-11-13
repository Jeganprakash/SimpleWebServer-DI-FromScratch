package com.jan.dev

import com.jan.dev.framework.factory.SingletonObjectsFactory
import com.jan.dev.framework.service.contracts.RequestHandler

fun main() {

    // TODO :: need to figure out a way to not hardcore package and classNames in main

    SingletonObjectsFactory.initiateSingletonObjects("com.jan.dev")
    val className = Class.forName("com.jan.dev.framework.service.implementations.SocketRequestHandler")
    println(className)
    val obj = SingletonObjectsFactory.getObject(Class.forName("com.jan.dev.framework.service.implementations.SocketRequestHandler"))
    (obj as RequestHandler).listenPort(3000)
}
