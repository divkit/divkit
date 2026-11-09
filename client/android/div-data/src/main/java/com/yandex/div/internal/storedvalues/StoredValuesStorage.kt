package com.yandex.div.internal.storedvalues

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.StoredValue
import com.yandex.div.data.StoredValue.ArrayStoredValue
import com.yandex.div.data.StoredValue.BooleanStoredValue
import com.yandex.div.data.StoredValue.ColorStoredValue
import com.yandex.div.data.StoredValue.DictStoredValue
import com.yandex.div.data.StoredValue.DoubleStoredValue
import com.yandex.div.data.StoredValue.IntegerStoredValue
import com.yandex.div.data.StoredValue.StringStoredValue
import com.yandex.div.data.StoredValue.Type.Converter
import com.yandex.div.data.StoredValue.UrlStoredValue
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import org.json.JSONException
import org.json.JSONObject

@InternalApi
class StoredValuesStorage(
    private val repository: StoredValuesRepository,
    private val currentTimeMillis: () -> Long = { System.currentTimeMillis() }
) {
    @Throws(StoredValueException::class)
    fun getValue(
        name: String,
        scope: StoredValueScope,
        cardId: String
    ): StoredValue? {
        val id = formatId(name = name, scope = scope, cardId = cardId)
        val jsonValue = repository.get(id) ?: return null

        if (isExpired(jsonValue) || isExpiredLegacy(jsonValue)) {
            repository.remove(id)
            return null
        }

        try {
            val typeName = jsonValue.getString(KEY_TYPE)
            val type = Converter.fromString(typeName)
                ?: throw StoredValueException("Invalid stored value type: $typeName")
            return jsonValue.toStoredValue(type, name)
        } catch (e: JSONException) {
            throw StoredValueException("Failed to load stored value: $name. ${e.message}")
        }
    }

    fun setValue(
        value: StoredValue,
        lifetime: Long,
        scope: StoredValueScope,
        cardId: String
    ) {
        repository.put(
            id = formatId(name = value.name, scope = scope, cardId = cardId),
            value = value.toJSONObject(lifetime)
        )
    }

    private fun isExpiredLegacy(value: JSONObject): Boolean {
        if (value.has(KEY_EXPIRATION_TIME)) {
            val expirationTime = value.getLong(KEY_EXPIRATION_TIME)
            return currentTimeMillis() >= expirationTime
        }
        return false
    }

    private fun isExpired(value: JSONObject): Boolean {
        if (value.has(KEY_TIMESTAMP) && value.has(KEY_LIFETIME)) {
            val timestamp = value.getLong(KEY_TIMESTAMP)
            val lifetime = value.getLong(KEY_LIFETIME)
            val currentTimeSeconds = currentTimeMillis() / 1000L
            return currentTimeSeconds - timestamp >= lifetime
        }
        return false
    }

    private fun StoredValue.toJSONObject(lifetime: Long): JSONObject {
        val value = when (this) {
            is ArrayStoredValue,
            is BooleanStoredValue,
            is DictStoredValue,
            is DoubleStoredValue,
            is IntegerStoredValue,
            is StringStoredValue -> getValue()

            is ColorStoredValue,
            is UrlStoredValue -> getValue().toString()
        }

        return JSONObject().apply {
            put(KEY_TYPE, Converter.toString(getType()))
            put(KEY_TIMESTAMP, currentTimeMillis() / 1000L)
            put(KEY_LIFETIME, lifetime)
            put(KEY_VALUE, value)
        }
    }
}

@InternalApi
fun isStoredValueForCard(storedValueId: String, cardId: String): Boolean {
    return when {
        cardId.isEmpty() -> !storedValueId.startsWith(CARD_PREFIX)
        else -> storedValueId.startsWith(formatCardPrefix(cardId))
    }
}

private const val KEY_EXPIRATION_TIME = "expiration_time"
private const val KEY_TIMESTAMP = "timestamp"
private const val KEY_LIFETIME = "lifetime"
private const val KEY_TYPE = "type"
private const val KEY_VALUE = "value"

@Throws(JSONException::class)
private fun JSONObject.toStoredValue(type: StoredValue.Type, name: String): StoredValue {
    return when (type) {
        StoredValue.Type.ARRAY -> ArrayStoredValue(name, getJSONArray(KEY_VALUE))
        StoredValue.Type.BOOLEAN -> BooleanStoredValue(name, getBoolean(KEY_VALUE))
        StoredValue.Type.COLOR -> ColorStoredValue(name, Color.parse(getString(KEY_VALUE)))
        StoredValue.Type.DICT -> DictStoredValue(name, getJSONObject(KEY_VALUE))
        StoredValue.Type.INTEGER -> IntegerStoredValue(name, getLong(KEY_VALUE))
        StoredValue.Type.NUMBER -> DoubleStoredValue(name, getDouble(KEY_VALUE))
        StoredValue.Type.STRING -> StringStoredValue(name, getString(KEY_VALUE))
        StoredValue.Type.URL -> UrlStoredValue(name, Url.from(getString(KEY_VALUE)))
    }
}

private const val CARD_PREFIX = "card_"

private fun formatId(
    name: String,
    scope: StoredValueScope,
    cardId: String
): String {
    val valueId = "stored_value_$name"
    return when (scope) {
        StoredValueScope.Global -> valueId
        StoredValueScope.Card -> "${formatCardPrefix(cardId)}$valueId"
    }
}

private fun formatCardPrefix(cardId: String): String = "$CARD_PREFIX${cardId}_"
