package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.Function

internal class FunctionImpl(
    override val name: String,
    override val declaredArgs: List<FunctionArgument>,
    override val isPure: Boolean = true
) : Function() {

    override val resultType = EvaluableType.BOOLEAN

    override fun evaluate(args: List<Any>) = true
}
