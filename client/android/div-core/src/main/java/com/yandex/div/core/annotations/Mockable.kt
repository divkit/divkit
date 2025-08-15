package com.yandex.div.core.annotations

/**
 * Used to annotate kotlin that can be mocked by mockito.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
public annotation class Mockable
