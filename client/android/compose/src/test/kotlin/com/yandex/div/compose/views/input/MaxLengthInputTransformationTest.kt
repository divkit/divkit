package com.yandex.div.compose.views.input

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import androidx.compose.ui.text.TextRange
import kotlin.test.Test
import kotlin.test.assertEquals

class MaxLengthInputTransformationTest {

    @Test
    fun `clipping insertion shifts cursor inside preserved suffix`() {
        val state = TextFieldState(initialText = "abcd", initialSelection = TextRange(2))

        state.edit {
            insert(2, "XYZ")
            selection = TextRange(6)
            with(MaxLengthInputTransformation(5)) { transformInput() }
        }

        assertEquals("abXcd", state.text.toString())
        assertEquals(TextRange(4), state.selection)
    }

    @Test
    fun `clipping insertion keeps cursor at end`() {
        val state = TextFieldState(initialText = "abcd", initialSelection = TextRange(2))

        state.edit {
            insert(2, "XYZ")
            selection = TextRange(7)
            with(MaxLengthInputTransformation(5)) { transformInput() }
        }

        assertEquals("abXcd", state.text.toString())
        assertEquals(TextRange(5), state.selection)
    }

    @Test
    fun `disjoint insertions preserve original text when limit is reached`() {
        val state = TextFieldState(initialText = "abcd")

        state.edit {
            insert(1, "XY")
            insert(5, "Z")
            with(MaxLengthInputTransformation(4)) { transformInput() }
        }

        assertEquals("abcd", state.text.toString())
    }

    @Test
    fun `left insertion keeps capacity when inserted before right insertion`() {
        val state = TextFieldState(initialText = "abcd")

        state.edit {
            insert(1, "XY")
            insert(5, "ZW")
            with(MaxLengthInputTransformation(6)) { transformInput() }
        }

        assertEquals("aXYbcd", state.text.toString())
    }

    @Test
    fun `left insertion keeps capacity when inserted after right insertion`() {
        val state = TextFieldState(initialText = "abcd")

        state.edit {
            insert(3, "ZW")
            insert(1, "XY")
            with(MaxLengthInputTransformation(6)) { transformInput() }
        }

        assertEquals("aXYbcd", state.text.toString())
    }
}
