package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.font.LineMetricsCache
import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.expressions.Expression.Companion.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivTextFontMeasurementTest {

    @get:Rule
    val rule = createComposeRule()

    private val configuration = DivConfiguration(reporter = TestReporter())

    private lateinit var cache: LineMetricsCache

    @Test
    fun `texts that differ only in color share a single measurement`() {
        setContent(
            (0 until 10).map { index ->
                text(
                    fontSize = 13,
                    lineHeight = 16,
                    text = constant("Sample"),
                    textColor = Color(0xFF000000.toInt() + index),
                )
            }
        )

        assertEquals(1, cache.size)
    }

    @Test
    fun `texts with different font sizes are measured separately`() {
        setContent(
            listOf(
                text(fontSize = 13, lineHeight = 16, text = constant("Sample")),
                text(fontSize = 13, lineHeight = 16, text = constant("Sample")),
                text(fontSize = 18, lineHeight = 22, text = constant("Sample")),
                text(fontSize = 18, lineHeight = 22, text = constant("Sample")),
            )
        )

        assertEquals(2, cache.size)
    }

    @Test
    fun `text without line height is not measured`() {
        setContent(listOf(text(fontSize = 13, text = constant("Sample"))))

        assertEquals(0, cache.size)
    }

    @Test
    fun `non positive line height is not measured`() {
        setContent(listOf(text(fontSize = 13, lineHeight = 0, text = constant("Sample"))))

        assertEquals(0, cache.size)
    }

    private fun setContent(items: List<Div>) {
        val divData = data(container(items = items))
        rule.setContent {
            val divContext = DivContext(baseContext = LocalContext.current, configuration = configuration)
            cache = divContext.component.lineMetricsCache
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data = divData)
            }
        }
        rule.waitForIdle()
    }
}
