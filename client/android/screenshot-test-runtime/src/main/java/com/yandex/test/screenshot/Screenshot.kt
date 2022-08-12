package com.yandex.test.screenshot

import androidx.annotation.IdRes

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Screenshot(
    @IdRes val viewId: Int,
    val relativePath: String = "",
    val name: String = ""
)
