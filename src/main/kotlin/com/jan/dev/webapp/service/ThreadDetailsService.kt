package com.jan.dev.webapp.service

import com.jan.dev.framework.annotation.SingletonObject
import java.lang.management.ManagementFactory

@SingletonObject
class ThreadDetailsService {
    fun getCurrentThreadDetails(): String {
        val threadMXBean = ManagementFactory.getThreadMXBean()
        val threadInfo = threadMXBean.getThreadInfo(Thread.currentThread().id)

        val threadDetails = """
                Hi, Welcome to Jan Server, Happy to Serve :)
                
                
                ---------------------------------------------------------------------------
                Current Request Thread Details 
                ----------------------------------------------------------------------------
                
                Thread Name: ${threadInfo.threadName}
                Thread ID: ${threadInfo.threadId}
                Thread State: ${threadInfo.threadState}
                Thread Priority: ${threadInfo.priority}
                
                --------------------------------------------------------------------------------
           """.trimIndent()

        return threadDetails
    }
}
