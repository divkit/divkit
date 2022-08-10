@file:JvmName("SerializationUtils")

package com.yandex.android.util

import androidx.collection.ArrayMap
import com.yandex.div.core.util.Assert

private const val MAP_KEY_VALUE_DELIMITER = '\t'
private const val MAP_KEY_VALUE_PAIR_DELIMITER = '\u0000'

internal fun Map<String, String>.serialize(): String? {
    if (isEmpty()) return null

    val stringBuilder = StringBuilder()
    forEach { (key, value) ->
        stringBuilder.append(key)
            .append(MAP_KEY_VALUE_DELIMITER)
            .append(value)
            .append(MAP_KEY_VALUE_PAIR_DELIMITER)
    }
    return stringBuilder.toString()
}

internal fun String?.deserializeMap(): Map<String, String> {
    if (this == null) return emptyMap()

    val pairs: List<String> = split(MAP_KEY_VALUE_PAIR_DELIMITER)
    if (pairs.isEmpty()) {
        Assert.fail("Incorrect serialization: empty map should be serialized into null value!")
        return emptyMap()
    }

    val result: MutableMap<String, String> = ArrayMap(pairs.size)
    for (i in pairs.indices) {
        val keyAndValue = pairs[i].split(MAP_KEY_VALUE_DELIMITER)
        if (keyAndValue.size == 1) {
            result[keyAndValue[0]] = ""
        } else {
            result[keyAndValue[0]] = keyAndValue[1]
        }
    }

    return result
}
