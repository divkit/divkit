package com.yandex.div.evaluable

import kotlin.jvm.JvmField

/**
 * Interface for providing functions.
 */
public interface FunctionProvider {

    public fun get(name: String, args: List<EvaluableType>): Function

    public fun getMethod(name: String, args: List<EvaluableType>): Function

    public companion object {
        @JvmField
        public val STUB: FunctionProvider = object : FunctionProvider {
            override fun get(name: String, args: List<EvaluableType>) = Function.STUB
            override fun getMethod(name: String, args: List<EvaluableType>): Function = Function.STUB
        }
    }
}
