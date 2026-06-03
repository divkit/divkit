package com.yandex.div.test.data

import androidx.core.net.toUri
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionArrayInsertValue
import com.yandex.div2.DivActionArrayRemoveValue
import com.yandex.div2.DivActionArraySetValue
import com.yandex.div2.DivActionCustom
import com.yandex.div2.DivActionDictSetValue
import com.yandex.div2.DivActionSetState
import com.yandex.div2.DivActionSetVariable
import com.yandex.div2.DivActionTimer
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivActionUpdateStructure
import com.yandex.div2.DivTypedValue
import org.json.JSONObject

fun action(
    id: String = "test",
    isEnabled: Boolean = true,
    menuItems: List<DivAction.MenuItem>? = null,
    payload: JSONObject? = null,
    typed: DivActionTyped? = null,
    url: String? = null,
): DivAction {
    return DivAction(
        isEnabled = constant(isEnabled),
        logId = constant(id),
        menuItems = menuItems,
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

fun customAction(): DivActionTyped = DivActionTyped.Custom(DivActionCustom())

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

fun setStateAction(
    stateId: Expression<String>,
    temporary: Boolean = true,
): DivActionTyped {
    return DivActionTyped.SetState(
        DivActionSetState(
            stateId = stateId,
            temporary = constant(temporary),
        )
    )
}

fun setStateAction(stateId: String, temporary: Boolean = true): DivActionTyped =
    setStateAction(stateId = constant(stateId), temporary = temporary)

fun setVariableAction(name: String, value: DivTypedValue): DivActionTyped {
    return DivActionTyped.SetVariable(
        DivActionSetVariable(value = value, variableName = constant(name))
    )
}

fun timerAction(id: String, action: DivActionTimer.Action): DivActionTyped {
    return DivActionTyped.Timer(
        DivActionTimer(
            id = constant(id),
            action = constant(action)
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

fun menuItem(
    action: DivAction,
    text: String
): DivAction.MenuItem {
    return DivAction.MenuItem(
        action = action,
        text = constant(text)
    )
}
