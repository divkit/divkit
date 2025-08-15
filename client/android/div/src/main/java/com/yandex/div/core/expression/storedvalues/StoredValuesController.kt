package com.yandex.div.core.expression.storedvalues

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.errors.ErrorCollector
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
import com.yandex.div.storage.DivStorageComponent
import com.yandex.div.storage.RawJsonRepository
import com.yandex.div.storage.RawJsonRepositoryException
import com.yandex.div.storage.rawjson.RawJson
import com.yandex.yatagan.Lazy
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

private const val STORED_VALUE_ID_PREFIX = "stored_value_"
private const val KEY_EXPIRATION_TIME = "expiration_time"
private const val KEY_TYPE = "type"
private const val KEY_VALUE = "value"

@Mockable
@DivScope
internal class StoredValuesController @Inject constructor(
    divStorageComponentLazy: Lazy<DivStorageComponent>,
) {
    private val rawJsonRepository by lazy { divStorageComponentLazy.get().rawJsonRepository }

    private val currentTime: Long
        get() = System.currentTimeMillis()

    fun getStoredValue(
        name: String,
        errorCollector: ErrorCollector? = null,
    ): StoredValue? {
        val storedValueId = STORED_VALUE_ID_PREFIX + name
        val repositoryResult = rawJsonRepository.get(listOf(storedValueId))

        errorCollector?.logRepositoryErrors(repositoryResult.errors)
        val jsonStoredValue = repositoryResult.resultData.firstOrNull()?.data ?: return null

        if (jsonStoredValue.has(KEY_EXPIRATION_TIME)) {
            val expirationTime = jsonStoredValue.getLong(KEY_EXPIRATION_TIME)
            if (currentTime >= expirationTime) {
                rawJsonRepository.remove { it.id == storedValueId }
                return null
            }
        }

        try {
            val typeStrValue = jsonStoredValue.getString(KEY_TYPE)
            val storedValueType = Converter.fromString(typeStrValue)
            if (storedValueType == null) {
                errorCollector.logUnknownType(name, typeStrValue)
                return null
            }
            return jsonStoredValue.toStoredValue(storedValueType, name)
        } catch (e: JSONException) {
            errorCollector.logDeclarationFailed(name, e)
            return null
        }
    }

    fun setStoredValue(
        storedValue: StoredValue,
        lifetime: Long,
        errorCollector: ErrorCollector? = null,
    ): Boolean {
        val rawPayload = RawJsonRepository.Payload(
            jsons = listOf(
                RawJson(
                    id = STORED_VALUE_ID_PREFIX + storedValue.name,
                    data = storedValue.toJSONObject(lifetime)
                )
            )
        )
        val repositoryResult = rawJsonRepository.put(rawPayload)
        errorCollector?.logRepositoryErrors(repositoryResult.errors)
        return repositoryResult.errors.isEmpty()
    }

    private fun ErrorCollector.logRepositoryErrors(errors: List<RawJsonRepositoryException>) {
        errors.forEach { error -> logError(error) }
    }

    private fun ErrorCollector?.logUnknownType(name: String, unknownType: String) {
        val declarationException = StoredValueDeclarationException(
            message = "Stored value '$name' declaration failed because of unknown type '$unknownType'"
        )
        this?.logError(declarationException)
    }

    private fun ErrorCollector?.logDeclarationFailed(name: String, cause: Throwable? = null) {
        val declarationException = StoredValueDeclarationException(
            message = "Stored value '$name' declaration failed: ${cause?.message}",
            cause = cause,
        )
        this?.logError(declarationException)
    }

    @Throws(JSONException::class)
    private fun JSONObject.toStoredValue(type: StoredValue.Type, name: String): StoredValue =
        when (type) {
            StoredValue.Type.STRING -> StringStoredValue(name, getString(KEY_VALUE))
            StoredValue.Type.INTEGER -> IntegerStoredValue(name, getLong(KEY_VALUE))
            StoredValue.Type.BOOLEAN -> BooleanStoredValue(name, getBoolean(KEY_VALUE))
            StoredValue.Type.NUMBER -> DoubleStoredValue(name, getDouble(KEY_VALUE))
            StoredValue.Type.COLOR -> ColorStoredValue(name, Color.parse(getString(KEY_VALUE)))
            StoredValue.Type.URL -> UrlStoredValue(name, Url.from(getString(KEY_VALUE)))
            StoredValue.Type.ARRAY -> ArrayStoredValue(name, getJSONArray(KEY_VALUE))
            StoredValue.Type.DICT -> DictStoredValue(name, getJSONObject(KEY_VALUE))
        }

    private fun StoredValue.toJSONObject(lifetime: Long): JSONObject {
        val value = when (this) {
            is StringStoredValue,
            is IntegerStoredValue,
            is BooleanStoredValue,
            is ArrayStoredValue,
            is DictStoredValue,
            is DoubleStoredValue -> getValue()
            is UrlStoredValue,
            is ColorStoredValue -> getValue().toString()
        }
        val json = JSONObject().apply {
            put(KEY_EXPIRATION_TIME, currentTime + lifetime * 1000)
            put(KEY_TYPE, Converter.toString(getType()))
            put(KEY_VALUE, value)
        }
        return json
    }
}
