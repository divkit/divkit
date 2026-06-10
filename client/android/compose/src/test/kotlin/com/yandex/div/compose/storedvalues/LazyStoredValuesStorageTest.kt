package com.yandex.div.compose.storedvalues

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.data.StoredValue
import com.yandex.div.internal.storedvalues.StoredValueScope
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class LazyStoredValuesStorageTest {
    private val reporter = TestReporter()
    private val repository = TestStoredValuesRepository()

    private val storage = LazyStoredValuesStorage(
        cardId = "test",
        reporter = reporter,
        repository = { repository }
    )

    @Test
    fun `getValue() returns null if value is not present`() {
        assertNull(getValue("unknown"))
    }

    @Test
    fun `setValue() sets string value`() {
        storage.setValue(
            value = StoredValue.StringStoredValue(name = "value", value = "stored value"),
            scope = StoredValueScope.Global,
            lifetime = ONE_HOUR
        )

        assertEquals("stored value", getValue("value"))
    }

    @Test
    fun `setValue() sets double value`() {
        storage.setValue(
            value = StoredValue.DoubleStoredValue(name = "value", value = 123.45),
            scope = StoredValueScope.Global,
            lifetime = ONE_HOUR
        )

        assertEquals(123.45, getValue("value"))
    }

    @Test
    fun `setValue() updates string value`() {
        storage.setValue(
            value = StoredValue.StringStoredValue(name = "value", value = "stored value"),
            scope = StoredValueScope.Global,
            lifetime = ONE_HOUR
        )

        storage.setValue(
            value = StoredValue.StringStoredValue(name = "value", value = "new value"),
            scope = StoredValueScope.Global,
            lifetime = ONE_HOUR
        )

        assertEquals("new value", getValue("value"))
    }

    @Test
    fun `setValue() updates value with different value type`() {
        storage.setValue(
            value = StoredValue.StringStoredValue(name = "value", value = "stored value"),
            scope = StoredValueScope.Global,
            lifetime = ONE_HOUR
        )

        storage.setValue(
            value = StoredValue.DoubleStoredValue(name = "value", value = 123.45),
            scope = StoredValueScope.Global,
            lifetime = ONE_HOUR
        )

        assertEquals(123.45, getValue("value"))
    }

    @Test
    fun `setValue() generates correct repository id for global value`() {
        storage.setValue(
            value = StoredValue.StringStoredValue(name = "value1", value = "stored value"),
            scope = StoredValueScope.Global,
            lifetime = ONE_HOUR
        )

        assertEquals(setOf("stored_value_value1"), repository.values.keys)
    }

    @Test
    fun `setValue() generates correct repository id for card value`() {
        storage.setValue(
            value = StoredValue.StringStoredValue(name = "value1", value = "stored value"),
            scope = StoredValueScope.Card,
            lifetime = ONE_HOUR
        )

        assertEquals(setOf("card_test_stored_value_value1"), repository.values.keys)
    }

    private fun getValue(name: String): Any? {
        return storage.getValue(name, StoredValueScope.Global)
    }
}

private const val ONE_HOUR = 3600L
