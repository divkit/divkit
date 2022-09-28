package com.yandex.div.core.view2.errors

import com.yandex.div.R

internal data class ErrorViewModel(
    val showDetails: Boolean = false,
    private val errorCount: Int = 0,
    private val warningCount: Int = 0,
    private val errorDetails: String = "",
    private val warningDetails: String = "",
) {
    fun getDetails(): String {
        return if (errorCount > 0 && warningCount > 0) {
            "${errorDetails}\n\n${warningDetails}"
        } else if (warningCount > 0) {
            warningDetails
        } else {
            errorDetails
        }
    }

    fun getCounterBackground(): Int {
        return if (warningCount > 0 && errorCount > 0) R.drawable.warning_error_counter_background
        else if (warningCount > 0) R.drawable.warning_counter_background
        else R.drawable.error_counter_background
    }

    fun getCounterText(): String {
        return if (errorCount > 0 && warningCount > 0) {
            "${errorCount}/${warningCount}"
        } else if (warningCount > 0) {
            warningCount.toString()
        } else if (errorCount > 0){
            errorCount.toString()
        } else ""
    }
}