package com.yandex.div.compose

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.showTooltipAction
import com.yandex.div.test.data.text
import com.yandex.div2.DivTooltip
import kotlin.test.Test
import kotlin.test.assertSame
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalContextTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var renderedContext: DivContext? = null

    private val customFactory = object : DivCustomViewFactory {
        @Composable
        override fun Content(modifier: Modifier, environment: DivCustomEnvironment) {
            renderedContext = LocalContext.current as DivContext
            Text("Legacy custom", modifier)
        }
    }

    private val divContext by lazy {
        DivContext(
            baseContext = rule.activity,
            configuration = divConfiguration {
                reporter = TestReporter()
                customViewFactories = mapOf("legacy" to customFactory)
            }
        )
    }

    private val card = data(custom(type = "legacy"))

    @Test
    fun `LocalContext provider renders custom content with its DivContext`() {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(card)
            }
        }

        rule.onNodeWithText("Legacy custom").assertIsDisplayed()
        assertSame(divContext, renderedContext)
    }

    @Test
    fun `ComposeView context renders custom content without an explicit provider`() {
        val view = ComposeView(divContext).apply {
            setContent { DivView(card) }
        }
        rule.activity.setContentView(view)

        rule.onNodeWithText("Legacy custom").assertIsDisplayed()
        assertSame(divContext, renderedContext)
    }

    @Test
    fun `DivViewHost with data preserves LocalContext for custom content`() {
        val host = DivViewHost(divContext)
        host.setContent(card)
        rule.activity.setContentView(host.composeView)

        rule.onNodeWithText("Legacy custom").assertIsDisplayed()
        assertSame(divContext, renderedContext)
    }

    @Test
    fun `DivViewHost without data preserves LocalContext for custom content`() {
        val host = DivViewHost(divContext)
        host.setContent { DivView(card) }
        rule.activity.setContentView(host.composeView)

        rule.onNodeWithText("Legacy custom").assertIsDisplayed()
        assertSame(divContext, renderedContext)
    }

    @Test
    fun `DivViewHost respects a nested LocalContext provider`() {
        val outerContext = contextWithValue("Outer")
        val innerContext = contextWithValue("Inner")
        val nestedCard = data(text(text = expression("@{value}")))
        val host = DivViewHost(outerContext)
        host.setContent {
            CompositionLocalProvider(LocalContext provides innerContext) {
                DivView(nestedCard)
            }
        }
        rule.activity.setContentView(host.composeView)

        rule.onNodeWithText("Inner").assertIsDisplayed()
    }

    @Test
    fun `LocalContext provider is preserved in tooltip custom content`() {
        val tooltipCard = data(text(
            text = "Show tooltip",
            action = action(typed = showTooltipAction(id = "tooltip")),
            tooltips = listOf(DivTooltip(
                id = "tooltip",
                position = constant(DivTooltip.Position.CENTER),
                div = custom(type = "legacy"),
            )),
        ))
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(tooltipCard)
            }
        }

        rule.onNodeWithText("Show tooltip").performClick()

        rule.onNodeWithText("Legacy custom").assertIsDisplayed()
        assertSame(divContext, renderedContext)
    }

    private fun contextWithValue(value: String) = DivContext(
        rule.activity,
        divConfiguration {
            reporter = TestReporter()
            variableController = DivVariableController().apply {
                declare(Variable.StringVariable("value", value))
            }
        },
    )
}
