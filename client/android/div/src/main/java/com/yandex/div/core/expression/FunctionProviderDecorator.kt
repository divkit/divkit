package com.yandex.div.core.expression

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionProvider
import com.yandex.div.evaluable.LocalFunctionProvider
import com.yandex.div.evaluable.MissingLocalFunctionException

internal class FunctionProviderDecorator(private val provider: FunctionProvider) : FunctionProvider {

    override fun get(name: String, args: List<EvaluableType>) = provider.get(name, args)

    override fun getMethod(name: String, args: List<EvaluableType>) = provider.getMethod(name, args)

    operator fun plus(functions: List<Function>): FunctionProviderDecorator {
        val localProvider = LocalFunctionProvider(functions)
        return FunctionProviderDecorator(
            object : FunctionProvider {

                override fun get(name: String, args: List<EvaluableType>): Function {
                    return try {
                        localProvider.get(name, args)
                    } catch (e: MissingLocalFunctionException) {
                        provider.get(name, args)
                    }
                }

                override fun getMethod(name: String, args: List<EvaluableType>): Function {
                    return try {
                        localProvider.getMethod(name, args)
                    } catch (e: MissingLocalFunctionException) {
                        provider.getMethod(name, args)
                    }
                }
            }
        )
    }
}
