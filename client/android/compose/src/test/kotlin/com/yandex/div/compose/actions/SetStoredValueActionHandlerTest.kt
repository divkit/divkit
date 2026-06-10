package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.storedvalues.LazyStoredValuesStorage
import com.yandex.div.compose.storedvalues.TestStoredValuesRepository
import com.yandex.div.compose.utils.SystemTimeProvider
import com.yandex.div.evaluable.types.Color
import com.yandex.div.internal.storedvalues.StoredValueScope
import com.yandex.div.test.data.action
import com.yandex.div.test.data.setStoredValueAction
import com.yandex.div.test.data.typedValue
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionSetStoredValue.Scope
import org.junit.runner.RunWith
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class SetStoredValueActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()

    private val storedValuesStorage = LazyStoredValuesStorage(
        cardId = actionHandlerEnvironment.context.cardId,
        reporter = reporter,
        repository = { TestStoredValuesRepository() },
        timeProvider = SystemTimeProvider()
    )

    private val reporter: TestReporter
        get() = actionHandlerEnvironment.reporter

    @BeforeTest
    fun setUp() {
        actionHandlerEnvironment.init(
            setStoredValueActionHandler = SetStoredValueActionHandler(
                reporter = reporter,
                storedValuesStorage = storedValuesStorage
            )
        )
    }

    @Test
    fun `set string value`() {
        handle(
            action(
                typed = setStoredValueAction(
                    name = "value",
                    value = typedValue("stored value"),
                    lifetime = ONE_HOUR
                )
            )
        )

        assertEquals("stored value", getValue("value"))
    }

    @Test
    fun `set string value with card scope`() {
        handle(
            action(
                typed = setStoredValueAction(
                    name = "value",
                    value = typedValue("stored value"),
                    lifetime = ONE_HOUR,
                    scope = Scope.CARD
                )
            )
        )

        assertEquals(
            "stored value",
            storedValuesStorage.getValue("value", StoredValueScope.Card)
        )

        assertNull(storedValuesStorage.getValue("value", StoredValueScope.Global))
    }

    @Test
    fun `set number value`() {
        handle(
            action(
                typed = setStoredValueAction(
                    name = "value",
                    value = typedValue(123.45),
                    lifetime = ONE_HOUR
                )
            )
        )

        assertEquals(123.45, getValue("value"))
    }

    @Test
    fun `div-action with string value`() {
        handle(
            action(url = "div-action://set_stored_value?name=value&type=string&value=stored value&lifetime=1000")
        )

        assertEquals("stored value", getValue("value"))
    }

    @Test
    fun `div-action with number value`() {
        handle(
            action(url = "div-action://set_stored_value?name=value&type=number&value=123.45&lifetime=1000")
        )

        assertEquals(123.45, getValue("value"))
    }

    @Test
    fun `div-action with boolean value`() {
        handle(
            action(url = "div-action://set_stored_value?name=value&type=boolean&value=true&lifetime=1000")
        )

        assertEquals(true, getValue("value"))
    }

    @Test
    fun `div-action with color value`() {
        handle(
            action(url = "div-action://set_stored_value?name=value&type=color&value=%23AABBCC&lifetime=1000")
        )

        assertEquals(Color.parse("#AABBCC"), getValue("value"))
    }

    @Test
    fun `div-action with invalid number value`() {
        reporter.failOnError = false

        handle(
            action(url = "div-action://set_stored_value?name=value&type=number&value=invalid value&lifetime=1000")
        )

        assertEquals("Failed to parse stored value: invalid value", reporter.lastError)
        assertNull(getValue("value"))
    }

    @Test
    fun `div-action with card scope value`() {
        handle(
            action(url = "div-action://set_stored_value?name=value&type=string&value=stored value&scope=card&lifetime=1000")
        )

        assertEquals(
            "stored value",
            storedValuesStorage.getValue("value", StoredValueScope.Card)
        )
    }

    private fun getValue(name: String): Any? {
        return storedValuesStorage.getValue(name, StoredValueScope.Global)
    }

    private fun handle(action: DivAction) = actionHandlerEnvironment.handle(action)
}

private const val ONE_HOUR = 3600L
