package com.yandex.div.rive

import app.rive.runtime.kotlin.core.Alignment
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.Loop
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
internal class CustomPropsParserTest(private val actual: String, private val expected: DivRiveCustomProps) {

    companion object {
        private val possibleAlignmentValues = arrayOf(
            DivRiveCustomProps.ALIGNMENT_TOP_LEFT, DivRiveCustomProps.ALIGNMENT_CENTER, DivRiveCustomProps.ALIGNMENT_TOP_RIGHT,
            DivRiveCustomProps.ALIGNMENT_CENTER_LEFT, DivRiveCustomProps.ALIGNMENT_CENTER, DivRiveCustomProps.ALIGNMENT_CENTER_RIGHT,
            DivRiveCustomProps.ALIGNMENT_BOTTOM_LEFT, DivRiveCustomProps.ALIGNMENT_BOTTOM_CENTER, DivRiveCustomProps.ALIGNMENT_BOTTOM_RIGHT
        )
        private val possibleLoopValues = arrayOf(DivRiveCustomProps.LOOP_ONE_SHOT, DivRiveCustomProps.LOOP_LOOP,
            DivRiveCustomProps.LOOP_PING_PONG, DivRiveCustomProps.LOOP_AUTO)
        private val possibleFitValues =
            arrayOf(DivRiveCustomProps.FIT_FILL, DivRiveCustomProps.FIT_CONTAIN, DivRiveCustomProps.FIT_COVER,
                DivRiveCustomProps.FIT_FIT_WIDTH, DivRiveCustomProps.FIT_FIT_HEIGHT, DivRiveCustomProps.FIT_NONE,
                DivRiveCustomProps.FIT_SCALE_DOWN)

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf("{}", DivRiveCustomProps.DEFAULT),
            arrayOf("""{ "url":"http://testUrl" }""".trimIndent(), DivRiveCustomProps.DEFAULT.copy(url = "http://testUrl"))
        ) + possibleLoopValues.map { loop ->
            arrayOf("""{"loop" : $loop}""", DivRiveCustomProps.DEFAULT.copy(loop = Loop.valueOf(loop.uppercase())))
        } + possibleFitValues.mapIndexed { index, fit ->
            arrayOf("""{"fit" : $fit}""", DivRiveCustomProps.DEFAULT.copy(fit = Fit.valueOf(fit.toEnumValueName())))
        } + possibleAlignmentValues.mapIndexed { index, alignment ->
            arrayOf("""{"alignment" : $alignment}""", DivRiveCustomProps.DEFAULT.copy(alignment = Alignment.valueOf(alignment.toEnumValueName())))
        }

        private fun String.toEnumValueName() = replace("(\\p{Ll})(\\p{Lu})".toRegex(), "$1_$2").uppercase()
    }

    @Test
    fun `assert constant value is correct`() {
        assertEquals(JSONObject(actual).riveAnimationProps(), expected)
    }
}
