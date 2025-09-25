package com.yandex.div.core

import com.yandex.div.DivDataTag
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div2.DivData

/**
 * Allows handling of runtime errors and warnings.
 * For parsing errors use the [ParsingErrorLogger].
 */
@PublicApi
interface DivErrorsReporter {

    /**
     * Error after parsing, occurring at render time.
     */
    fun onRuntimeError(
        divData: DivData?,
        divDataTag: DivDataTag,
        error: Throwable,
    )

    /**
     * Non-fatal runtime issue; execution continues (degraded or with fallback).
     */
    fun onWarning(
        divData: DivData?,
        divDataTag: DivDataTag,
        warning: Throwable,
    )

    companion object {
        @JvmField
        val STUB: DivErrorsReporter = object : DivErrorsReporter {
            override fun onRuntimeError(divData: DivData?, divDataTag: DivDataTag,error: Throwable) = Unit

            override fun onWarning(divData: DivData?, divDataTag: DivDataTag,warning: Throwable) = Unit
        }
    }
}
