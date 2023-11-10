package com.jan.dev

import com.jan.dev.framework.factory.SingletonObjectsFactory
import com.jan.dev.framework.service.contracts.RequestHandler

fun main() {
    SingletonObjectsFactory.initiateSingletonObjects("com.jan.dev")
    val obj = SingletonObjectsFactory.getObject(Class.forName("com.jan.dev.framework.service.implementations.SocketRequestHandler"))
    (obj as RequestHandler).listenPort(3000)
}
