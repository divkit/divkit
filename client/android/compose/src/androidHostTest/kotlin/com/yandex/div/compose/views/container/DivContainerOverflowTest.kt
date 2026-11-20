package com.yandex.div.compose.views.container

import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContent
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.text
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentVertical
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class DivContainerOverflowTest {

    @get:Rule
    val rule = createComposeRule()

    private val configuration = divConfiguration {
        reporter = TestReporter()
    }

    @Test
    fun `baseline row taller than the container starts at the top padding`() {
        setContent(
            DivContainer(
                contentAlignmentVertical = constant(DivContentAlignmentVertical.BASELINE),
                height = fixed(100),
                orientation = constant(DivContainer.Orientation.HORIZONTAL),
                paddings = insets(top = 20),
                items = listOf(
                    baselineText("lowered", topMargin = 60),
                    baselineText("raised", topMargin = 0),
                ),
            )
        )

        // The baselines match, so the row is 140 dp tall and "raised" starts 60 dp below its top.
        rule.onNodeWithTag("raised").assertTopPositionInRootIsEqualTo(80.dp)
    }

    private fun baselineText(id: String, topMargin: Int): Div = text(
        id = id,
        text = constant("Text"),
        width = wrapContent(),
        height = fixed(80),
        margins = insets(top = topMargin),
    )

    private fun setContent(container: DivContainer) {
        rule.setContent(configuration = configuration, data = data(Div.Container(container)))
    }
}
