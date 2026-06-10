package com.yandex.div.compose.storedvalues

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.dagger.Names
import com.yandex.div.compose.utils.TimeProvider
import com.yandex.div.data.StoredValue
import com.yandex.div.evaluable.ScopedStoredValueProvider
import com.yandex.div.internal.storedvalues.StoredValueException
import com.yandex.div.internal.storedvalues.StoredValueScope
import com.yandex.div.internal.storedvalues.StoredValuesRepository
import com.yandex.div.internal.storedvalues.StoredValuesStorage
import com.yandex.yatagan.Lazy
import javax.inject.Inject
import javax.inject.Named

@DivViewScope
internal class LazyStoredValuesStorage @Inject constructor(
    @param:Named(Names.CARD_ID) private val cardId: String,
    private val reporter: DivReporter,
    repository: Lazy<StoredValuesRepository>,
    timeProvider: TimeProvider
) : ScopedStoredValueProvider {

    private val storage by lazy {
        StoredValuesStorage(
            repository = repository.get(),
            currentTimeMillis = timeProvider::currentTimeMillis
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
        try {
            return storage.getValue(
                name = name,
                scope = scope,
                cardId = cardId
            )?.getValue()
        } catch (e: StoredValueException) {
            reporter.reportError(e.message)
        }
        return null
    }

    fun setValue(
        value: StoredValue,
        scope: StoredValueScope,
        lifetime: Long
    ) {
        storage.setValue(
            value = value,
            scope = scope,
            cardId = cardId,
            lifetime = lifetime
        )
    }
}
