package com.jan.dev.framework.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Controller(
    val path: String = "/"
)

@Retention(AnnotationRetention.RUNTIME)
@Target(*[AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS])
annotation class RequestPath(
    val path: String = "",
    val method: HttpMethod
)

enum class HttpMethod {
    GET,
    POST,
    PUT,
    HEAD,
    DELETE;

    companion object {
        fun contains(value: String): Boolean {
            return entries.any { it.name == value }
        }
    }
}
