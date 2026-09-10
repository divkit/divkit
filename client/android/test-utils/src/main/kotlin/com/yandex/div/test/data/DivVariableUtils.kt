package com.yandex.div.test.data

import com.yandex.div2.DivAction
import com.yandex.div2.DivEvaluableType
import com.yandex.div2.DivVariable
import com.yandex.div2.IntegerVariable
import com.yandex.div2.PropertyVariable
import com.yandex.div2.StrVariable

fun variable(name: String, value: Long): DivVariable {
    return DivVariable.Integer(IntegerVariable(name = name, value = constant(value)))
}

fun variable(name: String, value: String): DivVariable {
    return DivVariable.Str(StrVariable(name = name, value = constant(value)))
}

fun property(
    name: String,
    valueType: DivEvaluableType,
    get: String,
    set: List<DivAction>? = null,
    newValueVariableName: String = "new_value",
): DivVariable {
    return DivVariable.Property(
        PropertyVariable(
            name = name,
            get = constant(get),
            valueType = valueType,
            set = set,
            newValueVariableName = newValueVariableName
        )
    )
}

fun stringProperty(
    name: String,
    get: String,
    set: List<DivAction>? = null,
    newValueVariableName: String = "new_value",
): DivVariable {
    return property(
        name = name,
        valueType = DivEvaluableType.STRING,
        get = get,
        set = set,
        newValueVariableName = newValueVariableName
    )
}
