package com.yandex.divkit.multiplatform.state

import com.yandex.divkit.multiplatform.DivKit
import kotlin.reflect.KProperty

class DivKitVariableState<T : Any>(
    private val divKit: DivKit,
    private val name: String,
) {

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisObj: Any?, property: KProperty<*>): T {
        return divKit.getVariableValue(name) as? T ?: throw NoSuchElementException("Variable '$name' not found")
    }

    operator fun setValue(thisObj: Any?, property: KProperty<*>, value: T) {
        divKit.setVariable(name, value)
    }
}
