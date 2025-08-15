package com.yandex.div.evaluable

/**
 * Interface for providing functions.
 */
interface FunctionProvider {

    fun get(name: String, args: List<EvaluableType>): Function

    fun getMethod(name: String, args: List<EvaluableType>): Function

    companion object {
        @JvmField
        val STUB = object : FunctionProvider {
            override fun get(name: String, args: List<EvaluableType>) = Function.STUB
            override fun getMethod(name: String, args: List<EvaluableType>): Function = Function.STUB
        }
    }
}
