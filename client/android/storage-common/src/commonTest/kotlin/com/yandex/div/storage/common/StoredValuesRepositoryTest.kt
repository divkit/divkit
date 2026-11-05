package com.yandex.div.storage.common

import com.yandex.div.core.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class StoredValuesRepositoryTest {
    private val repository = InMemoryStoredValuesRepository()

    @Test
    fun `values can be stored replaced and removed`() {
        val first = JsonObject("""{"value":1}""")
        val replacement = JsonObject("""{"value":2}""")

        repository.put("value", first)
        assertSame(first, repository.get("value"))

        repository.put("value", replacement)
        assertSame(replacement, repository.get("value"))

        repository.remove("value")
        assertNull(repository.get("value"))
    }

    @Test
    fun `instances do not share stored values`() {
        repository.put("value", JsonObject("""{"value":1}"""))

        assertNull(InMemoryStoredValuesRepository().get("value"))
    }
}
