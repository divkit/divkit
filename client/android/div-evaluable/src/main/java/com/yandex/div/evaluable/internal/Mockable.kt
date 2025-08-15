package com.yandex.div.evaluable.internal

/**
 * Used to annotate kotlin that can be mocked by mockito.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Mockable
