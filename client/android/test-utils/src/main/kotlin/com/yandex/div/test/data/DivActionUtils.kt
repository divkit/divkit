package com.yandex.div.test.data

import androidx.core.net.toUri
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionArrayInsertValue
import com.yandex.div2.DivActionArrayRemoveValue
import com.yandex.div2.DivActionArraySetValue
import com.yandex.div2.DivActionDictSetValue
import com.yandex.div2.DivActionSetVariable
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivActionUpdateStructure
import com.yandex.div2.DivTypedValue
import org.json.JSONObject

fun action(
    typed: DivActionTyped? = null,
    payload: JSONObject? = null,
    url: String? = null,
): DivAction {
    return DivAction(
        logId = constant("test"),
        payload = payload,
        typed = typed,
        url = url?.let { constant(it.toUri()) }
    )
}

fun arrayInsertValueAction(
    name: String,
    index: Long? = null,
    value: DivTypedValue
): DivActionTyped {
    return DivActionTyped.ArrayInsertValue(
        DivActionArrayInsertValue(
            index = index?.let { constant(it) },
            value = value,
            variableName = constant(name)
        )
    )
}

fun arrayRemoveValueAction(
    name: String,
    index: Long
): DivActionTyped {
    return DivActionTyped.ArrayRemoveValue(
        DivActionArrayRemoveValue(
            index = constant(index),
            variableName = constant(name)
        )
    )
}

fun arraySetValueAction(
    name: String,
    index: Long,
    value: DivTypedValue
): DivActionTyped {
    return DivActionTyped.ArraySetValue(
        DivActionArraySetValue(
            index = constant(index),
            value = value,
            variableName = constant(name)
        )
    )
}

fun dictSetValueAction(
    name: String,
    key: String,
    value: DivTypedValue? = null
): DivActionTyped {
    return DivActionTyped.DictSetValue(
        DivActionDictSetValue(
            key = constant(key),
            value = value,
            variableName = constant(name)
        )
    )
}

fun updateStructureAction(
    name: String,
    path: String,
    value: DivTypedValue
): DivActionTyped {
    return DivActionTyped.UpdateStructure(
        DivActionUpdateStructure(
            path = constant(path),
            value = value,
            variableName = constant(name)
        )
    )
}

fun setVariableAction(name: String, value: DivTypedValue): DivActionTyped {
    return DivActionTyped.SetVariable(
        DivActionSetVariable(value = value, variableName = constant(name))
    )
}
