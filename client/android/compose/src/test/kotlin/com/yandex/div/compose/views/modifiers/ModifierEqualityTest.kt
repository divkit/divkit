package com.yandex.div.compose.views.modifiers

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actions.observedActions
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.test.data.accessibility
import com.yandex.div.test.data.action
import com.yandex.div.test.data.color
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.disappearAction
import com.yandex.div.test.data.fixedPivot
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.solidBackground
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityAction
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivTransform
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * For optimal performance, we need to make sure that on recomposition equal modifiers are created
 * if the data does not change.
 */
@RunWith(AndroidJUnit4::class)
class ModifierEqualityTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val configuration = DivComposeConfiguration(
        reporter = TestReporter()
    )

    @Test
    fun simple() {
        assertModifiersEqual(
            text(text = "Simple element")
        )
    }

    @Test
    fun paddings() {
        assertModifiersEqual(
            text(
                text = "Element with paddings",
                paddings = insets(
                    start = 10,
                    end = 10,
                    top = 20
                )
            )
        )
    }

    @Test
    fun margins() {
        assertModifiersEqual(
            text(
                text = "Element with margins",
                margins = insets(
                    start = 10,
                    end = 10,
                    top = 20
                )
            )
        )
    }

    @Test
    fun actions() {
        assertModifiersEqual(
            text(
                text = "Element with action",
                action = action(url = "test://")
            )
        )
    }

    @Test
    fun `visibility actions`() {
        assertModifiersEqual(
            text(
                text = "Element with action",
                visibilityActions = listOf(visibilityAction(url = "test://"))
            )
        )
    }

    @Test
    fun `disappear actions`() {
        assertModifiersEqual(
            text(
                text = "Element with action",
                disappearActions = listOf(disappearAction(url = "test://"))
            )
        )
    }

    @Test
    fun `accessibility description`() {
        assertModifiersEqual(
            text(
                text = "Element with accessibility",
                accessibility = accessibility(
                    description = "Description",
                    type = DivAccessibility.Type.HEADER
                )
            )
        )
    }

    @Test
    fun transform() {
        assertModifiersEqual(
            text(
                text = "Element with transform",
                transform = DivTransform(
                    rotation = constant(45.0),
                    pivotX = fixedPivot(100)
                )
            )
        )
    }

    @Test
    fun `solid background`() {
        assertModifiersEqual(
            container(
                backgrounds = listOf(
                    solidBackground(color = color(0xFFAABBCC))
                )
            )
        )
    }

    private fun assertModifiersEqual(div: Div) {
        var counter by mutableStateOf(0)
        var initialModifier: Modifier? = null
        var modifierOnRecomposition: Modifier? = null

        composeRule.setContent {
            val divContext = DivContext(
                baseContext = LocalContext.current,
                configuration = configuration
            )
            val viewContext = divContext.getViewContext(data(content = div))
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalDivViewContext provides viewContext,
                LocalComponent provides viewContext.rootLocalComponent
            ) {
                val modifier = div.modifier()
                if (counter == 0) {
                    initialModifier = modifier
                } else {
                    modifierOnRecomposition = modifier
                }
            }
        }

        counter++
        composeRule.waitForIdle()

        assertEquals(initialModifier, modifierOnRecomposition)
    }

    @Composable
    @SuppressLint("ModifierFactoryExtensionFunction")
    private fun Div.modifier(): Modifier {
        return Modifier.apply(
            div = this,
            actions = observedActions(),
            visibility = value().visibility.observedValue(),
            applyMargins = true
        )
    }
}
