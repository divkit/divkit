package com.yandex.div.core.annotations

/**
 * Marks types that are part of public API.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class PublicApi
