package com.yandex.div.compose.storedvalues

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.internal.storedvalues.StoredValuesRepository
import com.yandex.yatagan.Lazy
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@DivContextScope
@OptIn(ExperimentalTime::class)
internal class DivStoredValuesStorageFactory @Inject constructor(
    private val clock: Clock,
    private val reporter: DivReporter,
    private val repository: Lazy<StoredValuesRepository>,
) {
    fun create(cardId: String): DivStoredValuesStorage {
        return LazyStoredValuesStorage(
            cardId = cardId,
            clock = clock,
            reporter = reporter,
            repository = repository,
        )
    }
}
