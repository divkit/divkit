package com.yandex.div.internal.storedvalues

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StoredValuesStorageTest {

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
