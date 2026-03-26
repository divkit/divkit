package com.yandex.div.internal.expressions

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionProvider
import com.yandex.div.evaluable.LocalFunctionProvider
import com.yandex.div.evaluable.MissingLocalFunctionException

@InternalApi
class FunctionProviderDecorator(private val provider: FunctionProvider) : FunctionProvider {

    override fun get(name: String, args: List<EvaluableType>) = provider.get(name, args)

    override fun getMethod(name: String, args: List<EvaluableType>) = provider.getMethod(name, args)

    operator fun plus(functions: List<Function>): FunctionProviderDecorator {
        if (functions.isEmpty()) {
            return this
        }

        val localProvider = LocalFunctionProvider(functions)
        return FunctionProviderDecorator(
            object : FunctionProvider {

                override fun get(name: String, args: List<EvaluableType>): Function {
                    return try {
                        localProvider.get(name, args)
                    } catch (_: MissingLocalFunctionException) {
                        provider.get(name, args)
                    }
                }

                override fun getMethod(name: String, args: List<EvaluableType>): Function {
                    return try {
                        localProvider.getMethod(name, args)
                    } catch (_: MissingLocalFunctionException) {
                        provider.getMethod(name, args)
                    }
                }
            }
        )
    }
}
