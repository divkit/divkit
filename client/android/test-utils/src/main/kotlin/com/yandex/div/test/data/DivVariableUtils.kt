package com.yandex.div.test.data

import com.yandex.div2.DivVariable
import com.yandex.div2.IntegerVariable
import com.yandex.div2.StrVariable

fun variable(name: String, value: Long): DivVariable {
    return DivVariable.Integer(IntegerVariable(name = name, value = constant(value)))
}

fun variable(name: String, value: String): DivVariable {
    return DivVariable.Str(StrVariable(name = name, value = constant(value)))
}
