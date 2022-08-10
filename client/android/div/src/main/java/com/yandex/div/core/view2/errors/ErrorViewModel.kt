package com.yandex.div.core.view2.errors

internal data class ErrorViewModel(
    val showDetails: Boolean = false,
    val errorCount: Int = 0,
    val errorDetails: String = "",
)