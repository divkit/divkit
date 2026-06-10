package com.yandex.div.core.expression.storedvalues

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.StoredValue
import com.yandex.div.internal.storedvalues.StoredValueException
import com.yandex.div.internal.storedvalues.StoredValueScope
import com.yandex.div.internal.storedvalues.StoredValuesStorage
import com.yandex.div.internal.storedvalues.toStoredValueScope
import com.yandex.div.storage.DivStorageComponent
import com.yandex.div.storage.storedvalues.StoredValuesRepositoryImpl
import com.yandex.div2.DivActionSetStoredValue
import com.yandex.yatagan.Lazy
import javax.inject.Inject

@Mockable
@DivScope
internal class StoredValuesController @Inject constructor(
    storageComponent: Lazy<DivStorageComponent>
) {
    private val storage by lazy {
        StoredValuesStorage(
            repository = StoredValuesRepositoryImpl(
                rawJsonRepository = storageComponent.get().rawJsonRepository
            )
        )
    }

    fun getStoredValue(
        name: String,
        scope: String,
        dataTag: String,
        errorCollector: ErrorCollector
    ): StoredValue? {
        val divScope = DivActionSetStoredValue.Scope.fromString(scope) ?: run {
            errorCollector.logError(
                RuntimeException("Failed to get stored value: $name. Invalid scope: $scope")
            )
            return null
        }

        try {
            return storage.getValue(
                name = name,
                scope = divScope.toStoredValueScope(),
                cardId = dataTag
            )
        } catch (e: StoredValueException) {
            errorCollector.logError(e)
        }

        return null
    }

    fun setStoredValue(
        value: StoredValue,
        lifetime: Long,
        scope: StoredValueScope,
        dataTag: String,
        errorCollector: ErrorCollector
    ) {
        storage.setValue(
            value = value,
            lifetime = lifetime,
            scope = scope,
            cardId = dataTag,
        )
    }
}
