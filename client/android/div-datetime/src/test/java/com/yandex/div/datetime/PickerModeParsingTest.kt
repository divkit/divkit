package com.yandex.div.datetime

import com.yandex.div.datetime.data.PickerMode
import com.yandex.div.datetime.utils.parseMode
import com.yandex.div.json.ParsingException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal class PickerModeParsingTest(
    private val input: String,
    private val expected: Set<PickerMode>
) {

    @Test
    fun `GIVEN input WHEN parse THEN return expected picker modes`() {
        assertEquals(expected, parseMode(input))
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() = listOf(
            arrayOf(
                "date",
                setOf(PickerMode.DATE)
            ),
            arrayOf(
                "time",
                setOf(PickerMode.TIME)
            ),
            arrayOf(
                "date|time",
                setOf(PickerMode.DATE, PickerMode.TIME)
            ),
            arrayOf(
                "time|date",
                setOf(PickerMode.DATE, PickerMode.TIME)
            ),
            arrayOf(
                "space",
                emptySet<PickerMode>()
            )
        )
    }
}
