package com.yandex.test.screenshot

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Screenshot(
    val viewTag: String = SCREENSHOT_VIEW_TAG,
    val relativePath: String = "",
)
