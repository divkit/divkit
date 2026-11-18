package com.yandex.div.compose.storedvalues

import com.yandex.div.compose.DivContext
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.data.StoredValue

/**
 * Reads and writes already evaluated [StoredValue]s in the storage shared with
 * `set_stored_value` actions.
 *
 * Obtain a card-bound storage from [DivContext.getStoredValuesStorage],
 * [DivCustomEnvironment.storedValuesStorage], or [DivExtensionEnvironment.storedValuesStorage].
 */
interface DivStoredValuesStorage {
    /**
     * Returns a stored value with [name], or `null` if it does not exist or has expired.
     *
     * [DivStoredValueScope.Card] uses the card ID bound when obtaining this storage.
     * [DivStoredValueScope.Global] is shared by all cards.
     */
    fun getStoredValue(
        name: String,
        scope: DivStoredValueScope = DivStoredValueScope.Global,
    ): StoredValue?

    /**
     * Stores [value] in [scope] for [lifetime] seconds.
     *
     * [DivStoredValueScope.Card] uses the card ID bound when obtaining this storage.
     * [DivStoredValueScope.Global] is shared by all cards.
     */
    fun setValue(
        value: StoredValue,
        scope: DivStoredValueScope,
        lifetime: Long,
    )
}

/** Scope used to store and retrieve a value. */
enum class DivStoredValueScope {
    /** Shared by all cards. */
    Global,

    /** Bound to the card ID used to obtain the storage. */
    Card,
}
