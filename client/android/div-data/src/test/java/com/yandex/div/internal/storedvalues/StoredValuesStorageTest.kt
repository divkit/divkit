package com.yandex.div.internal.storedvalues

import com.yandex.div.core.json.JsonObject
import com.yandex.div.storage.common.InMemoryStoredValuesRepository
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StoredValuesStorageTest {

    @Test
    fun `legacy expiration time removes the envelope at its boundary`() {
        val repository = InMemoryStoredValuesRepository()
        val storage = StoredValuesStorage(repository) { 1000L }
        repository.put(
            "stored_value_value",
            JsonObject("""{"type":"string","value":"old","expiration_time":1000}""")
        )

        assertNull(storage.getValue("value", StoredValueScope.Global, "card"))
        assertNull(repository.get("stored_value_value"))
    }

    @Test
    fun `isStoredValueForCard() returns true when scope equals to card id`() {
        assertTrue(
            isStoredValueForCard(
                storedValueId = "card_cardId_stored_value_name",
                cardId = "cardId"
            )
        )
    }

    @Test
    fun `isStoredValueForCard() returns true when scope is empty and value has no scope`() {
        assertTrue(
            isStoredValueForCard(
                storedValueId = "stored_value_name",
                cardId = ""
            )
        )
    }

    @Test
    fun `isStoredValueForCard() returns false when scope does not equal to card id`() {
        assertFalse(
            isStoredValueForCard(
                storedValueId = "card_cardId_stored_value_name",
                cardId = "anotherCardId"
            )
        )
    }

    @Test
    fun `isStoredValueForCard() returns false when scope is empty and value has scope`() {
        assertFalse(
            isStoredValueForCard(
                storedValueId = "card_cardId_stored_value_name",
                cardId = ""
            )
        )
    }
}
