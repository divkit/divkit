package com.yandex.div.compose.storedvalues

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.dagger.Names
import com.yandex.div.data.StoredValue
import com.yandex.div.evaluable.ScopedStoredValueProvider
import com.yandex.div.internal.storedvalues.StoredValueException
import com.yandex.div.internal.storedvalues.StoredValueScope
import com.yandex.div.internal.storedvalues.StoredValuesRepository
import com.yandex.div.internal.storedvalues.StoredValuesStorage
import com.yandex.yatagan.Lazy
import javax.inject.Inject
import javax.inject.Named
import org.json.JSONException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@DivViewScope
@OptIn(ExperimentalTime::class)
internal class LazyStoredValuesStorage @Inject constructor(
    @param:Named(Names.CARD_ID) private val cardId: String,
    clock: Clock,
    private val reporter: DivReporter,
    repository: Lazy<StoredValuesRepository>,
) : ScopedStoredValueProvider, DivStoredValuesStorage {

    private val storage by lazy {
        StoredValuesStorage(
            repository = repository.get(),
            currentTimeMillis = { clock.now().toEpochMilliseconds() },
        )
    }

    override fun get(name: String, scope: String): Any? {
        return getValue(
            name = name,
            scope = StoredValueScope.fromString(scope)
        )
    }

    fun getValue(
        name: String,
        scope: StoredValueScope
    ): Any? {
        return getStoredValue(name, scope)?.getValue()
    }

    override fun getStoredValue(
        name: String,
        scope: DivStoredValueScope,
    ): StoredValue? {
        return getStoredValue(name, scope.toInternalScope())
    }

    private fun getStoredValue(
        name: String,
        scope: StoredValueScope,
    ): StoredValue? {
        try {
            return storage.getValue(
                name = name,
                scope = scope,
                cardId = cardId,
            )
        } catch (e: StoredValueException) {
            reporter.reportError(e.message)
        }
        return null
    }

    override fun setValue(
        value: StoredValue,
        scope: DivStoredValueScope,
        lifetime: Long,
    ) {
        if (value is StoredValue.DoubleStoredValue && !value.value.isFinite()) {
            reporter.reportError("Failed to store value '${value.name}': number must be finite.")
            return
        }

        try {
            setValue(value, scope.toInternalScope(), lifetime)
        } catch (e: JSONException) {
            reporter.reportError("Failed to store value '${value.name}': ${e.message}")
        }
    }

    fun setValue(
        value: StoredValue,
        scope: StoredValueScope,
        lifetime: Long,
    ) {
        storage.setValue(
            value = value,
            scope = scope,
            cardId = cardId,
            lifetime = lifetime,
        )
    }
}

private fun DivStoredValueScope.toInternalScope(): StoredValueScope {
    return when (this) {
        DivStoredValueScope.Global -> StoredValueScope.Global
        DivStoredValueScope.Card -> StoredValueScope.Card
    }
}
