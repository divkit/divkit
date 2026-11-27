package com.yandex.div.compose.focus

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InputFocusClearTest {
    @Test
    fun `clear keeps focus moved to another element during a blur callback`() {
        val inputFocus = InputFocus()
        var focusInFirstWindow: String? = "first input"
        var focusInSecondWindow: String? = "second input"
        val secondInput = InputFocus.FocusedInput { focusInSecondWindow = null }
        val firstInput = object : InputFocus.FocusedInput {
            override fun clearFocus() {
                focusInFirstWindow = null
                inputFocus.unregister(this)
                inputFocus.unregister(secondInput)
                focusInSecondWindow = "button"
            }
        }
        inputFocus.register(firstInput)
        inputFocus.register(secondInput)

        inputFocus.clear()

        assertNull(focusInFirstWindow)
        assertEquals("button", focusInSecondWindow)
    }
}
