package com.jan.dev

import com.jan.dev.framework.factory.SingletonObjectsFactory
import com.jan.dev.framework.service.abstracts.Server

fun main() {

    // TODO :: need to figure out a way to not hardcore package and classNames in main

    SingletonObjectsFactory.initiateSingletonObjects("com.jan.dev")
    val obj = SingletonObjectsFactory.getObject(Class.forName("com.jan.dev.framework.service.implementations.HttpServer"))
    (obj as Server).listenPort(3000)
}
