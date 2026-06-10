package com.yandex.div.internal.storedvalues

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div2.DivActionSetStoredValue

@InternalApi
enum class StoredValueScope {
    Global,
    Card;

    companion object {
        fun fromString(value: String?): StoredValueScope {
            return when (value) {
                "card" -> Card
                else -> Global
            }
        }
    }
}

@InternalApi
fun DivActionSetStoredValue.Scope?.toStoredValueScope(): StoredValueScope {
    return when (this) {
        DivActionSetStoredValue.Scope.CARD -> StoredValueScope.Card
        DivActionSetStoredValue.Scope.GLOBAL, null -> StoredValueScope.Global
    }
}
