package com.yandex.test.screenshot

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Screenshot(
    val viewTag: String,
    val relativePath: String = "",
    val name: String = ""
)
