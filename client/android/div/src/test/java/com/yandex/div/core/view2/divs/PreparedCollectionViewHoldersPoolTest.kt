package com.yandex.div.core.view2.divs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PreparedCollectionViewHoldersPoolTest {
    private val pool = PreparedCollectionViewHoldersPool()

    @Test
    fun `active and incoming contexts are reported when collection overlaps`() {
        val active = PreparedCollectionViewHoldersPool.BindingBatch(collectionId = 1, generation = 10)
        val incoming = PreparedCollectionViewHoldersPool.BindingBatch(collectionId = 1, generation = 10)

        val error = assertFailsWith<IllegalStateException> {
            pool.collect(active) { pool.collect(incoming) {} }
        }

        assertEquals(
            "Cannot collect overlapping binding batches: " +
                "active=BindingBatch@${System.identityHashCode(active)}(collectionId=1, generation=10), " +
                "incoming=BindingBatch@${System.identityHashCode(incoming)}(collectionId=1, generation=10)",
            error.message,
        )
    }
}
