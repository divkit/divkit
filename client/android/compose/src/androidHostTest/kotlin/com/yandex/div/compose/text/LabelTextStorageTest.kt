package com.yandex.div.compose.text

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class LabelTextStorageTest {
    private val storage = LabelTextStorage()
    private val first = storage.label("id")
    private val second = storage.label("id")

    @Test
    fun `set text is the text of the id`() {
        first.set("First")

        assertEquals("First", storage["id"])
    }

    @Test
    fun `label updates its text`() {
        first.set("First")

        first.set("Updated")

        assertEquals("Updated", storage["id"])
    }

    @Test
    fun `first label that set a text is the text of the id`() {
        first.set("First")

        second.set("Second")

        assertEquals("First", storage["id"])
    }

    @Test
    fun `removed first label hands over to the second`() {
        first.set("First")
        second.set("Second")

        first.remove()

        assertEquals("Second", storage["id"])
    }

    @Test
    fun `removed second label keeps the first`() {
        first.set("First")
        second.set("Second")

        second.remove()

        assertEquals("First", storage["id"])
    }

    @Test
    fun `removed last label removes the text`() {
        first.set("First")

        first.remove()

        assertNull(storage["id"])
    }

    @Test
    fun `forgotten label withdraws its text`() {
        first.set("First")

        first.onForgotten()

        assertNull(storage["id"])
    }

    @Test
    fun `removing a label that set no text changes nothing`() {
        first.set("First")

        second.remove()

        assertEquals("First", storage["id"])
    }
}
