package com.yandex.div.compose

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.showTooltipAction
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivTooltip
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class DivViewWithTooltipsTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val activity: ComponentActivity
        get() = rule.activity

    @Test
    fun `tooltip is shown`() {
        setContent(
            text(
                text = "Show tooltip",
                action = action(
                    typed = showTooltipAction(id = "tooltip")
                ),
                tooltips = listOf(
                    DivTooltip(
                        id = "tooltip",
                        position = constant(DivTooltip.Position.CENTER),
                        div = text(
                            text = "Tooltip text"
                        )
                    )
                )
            )
        )

        rule.onNodeWithText("Tooltip text").assertDoesNotExist()

        rule.onNodeWithText("Show tooltip").performClick()

        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    private fun setContent(content: Div) {
        activity.setContentView(
            ComposeView(
                context = DivContext(
                    baseContext = activity,
                    configuration = DivComposeConfiguration(
                        reporter = TestReporter()
                    )
                )
            ).apply {
                setContent {
                    DivView(data(content))
                }
            }
        )
    }
}
