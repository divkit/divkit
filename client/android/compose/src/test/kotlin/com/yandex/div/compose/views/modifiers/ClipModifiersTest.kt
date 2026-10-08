package com.yandex.div.compose.views.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.border
import com.yandex.div.test.data.container
import com.yandex.div.test.data.cornersRadius
import com.yandex.div2.DivExtension
import com.yandex.div2.DivFocus
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class ClipModifiersTest {

    @Test
    fun `clipToBounds() not applied for element with border with cornerRadius`() {
        val data = container(border = border(cornerRadius = 16))

        assertSame(
            Modifier,
            Modifier.clipToBounds(data.value())
        )
    }

    @Test
    fun `clipToBounds() not applied for element with border with zero cornerRadius`() {
        val data = container(border = border(cornerRadius = 0))

        assertSame(
            Modifier,
            Modifier.clipToBounds(data.value())
        )
    }

    @Test
    fun `clipToBounds() not applied for element with border with cornersRadius`() {
        val data = container(
            border = border(
                cornersRadius = cornersRadius(topLeft = 16)
            )
        )

        assertSame(
            Modifier,
            Modifier.clipToBounds(data.value())
        )
    }

    @Test
    fun `clipToBounds() is applied for element with simple border`() {
        val data = container(border = border())

        assertEquals(
            Modifier.clipToBounds(),
            Modifier.clipToBounds(data.value())
        )
    }

    @Test
    fun `clipToBounds() is applied for element without border`() {
        val data = container()

        assertEquals(
            Modifier.clipToBounds(),
            Modifier.clipToBounds(data.value())
        )
    }

    @Test
    fun `clipToBounds() is applied for element with focus border`() {
        val data = container(
            border = border(cornerRadius = 16),
            focus = DivFocus(border = border(cornerRadius = 0)),
        )

        assertEquals(
            Modifier.clipToBounds(),
            Modifier.clipToBounds(data.value())
        )
    }

    @Test
    fun `clipToBounds() is applied for element with extension`() {
        val data = container(
            border = border(cornerRadius = 16),
            extensions = listOf(DivExtension(id = "wrapper")),
        )

        assertEquals(
            Modifier.clipToBounds(),
            Modifier.clipToBounds(data.value())
        )
    }
}
