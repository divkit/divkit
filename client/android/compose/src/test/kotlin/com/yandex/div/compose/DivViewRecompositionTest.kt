package com.yandex.div.compose

import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.action
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.variable
import com.yandex.div2.DivData
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewRecompositionTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val mutableData = mutableStateOf(data(text(text = "")))

    private val activity: ComponentActivity
        get() = rule.activity

    private lateinit var divContext: DivContext

    @Before
    fun setUp() {
        divContext = DivContext(
            baseContext = activity,
            configuration = DivComposeConfiguration(
                reporter = TestReporter()
            )
        )
    }

    @Test
    fun `content changes when data is updated`() {
        mutableData.value = data(text(text = "Data 1"))

        setContent(mutableData)

        rule.onNodeWithText("Data 1").assertIsDisplayed()

        mutableData.value = data(text(text = "Data 2"))

        rule.onNodeWithText("Data 2").assertIsDisplayed()
    }

    @Test
    fun `new variables are used when data is updated`() {
        mutableData.value = data(
            text(text = expression("Data @{counter}")),
            variables = listOf(variable("counter", 1))
        )

        setContent(mutableData)

        rule.onNodeWithText("Data 1").assertIsDisplayed()

        mutableData.value = data(
            text(text = expression("Data @{counter}")),
            variables = listOf(variable("counter", 2))
        )

        rule.onNodeWithText("Data 2").assertIsDisplayed()
    }

    @Test
    fun `variable values are restored when data is switched back`() {
        val data = data(
            text(
                action = action(
                    typed = setVariableAction(name = "counter", value = typedValue(5))
                ),
                id = "button",
                text = expression("counter = @{counter}")
            ),
            variables = listOf(variable("counter", 1))
        )
        mutableData.value = data

        setContent(mutableData)

        rule.onNodeWithTag("button").apply {
            assertTextEquals("counter = 1")
            performClick()
            assertTextEquals("counter = 5")
        }

        mutableData.value = data(text(text = "New Data"))

        rule.onNodeWithText("New Data").assertIsDisplayed()

        mutableData.value = data

        rule.onNodeWithTag("button").assertTextEquals("counter = 5")
    }

    @Test
    fun `variable values are restored when ComposeView is recreated with the same context`() {
        val data = data(
            text(
                action = action(
                    typed = setVariableAction(name = "counter", value = typedValue(5))
                ),
                id = "button",
                text = expression("counter = @{counter}")
            ),
            variables = listOf(variable("counter", 1))
        )

        setContent(data)

        rule.onNodeWithTag("button").apply {
            assertTextEquals("counter = 1")
            performClick()
            assertTextEquals("counter = 5")
        }

        activity.setContentView(View(activity))
        setContent(data)

        rule.onNodeWithTag("button").assertTextEquals("counter = 5")
    }

    @Test
    fun `variable values are not restored when ComposeView is recreated and context is cleared`() {
        val data = data(
            text(
                action = action(
                    typed = setVariableAction(name = "counter", value = typedValue(5))
                ),
                id = "button",
                text = expression("counter = @{counter}")
            ),
            variables = listOf(variable("counter", 1))
        )

        setContent(data)

        rule.onNodeWithTag("button").apply {
            assertTextEquals("counter = 1")
            performClick()
            assertTextEquals("counter = 5")
        }

        activity.setContentView(View(activity))
        divContext.clearViewContext(data)
        setContent(data)

        rule.onNodeWithTag("button").assertTextEquals("counter = 1")
    }

    private fun setContent(data: DivData) = setContent(mutableStateOf(data))

    private fun setContent(data: State<DivData>) {
        activity.setContentView(
            ComposeView(divContext).apply {
                setContent {
                    DivView(data.value)
                }
            }
        )
    }
}
