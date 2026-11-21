package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContent
import com.yandex.div.test.data.container
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivSize
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class DivContainerOverlapTest {

    @get:Rule
    val rule = createComposeRule()

    private val configuration = divConfiguration {
        customViewFactories = mapOf("constrained_box" to ConstrainedBoxFactory())
        reporter = TestReporter()
    }

    @Test
    fun `custom child built on BoxWithConstraints defines the wrap_content width`() {
        setContent(
            overlap(
                width = wrapContent(),
                items = listOf(
                    custom(type = "constrained_box", width = wrapContent(), height = wrapContent()),
                    container(id = "fill", width = matchParent(), height = fixed(10)),
                ),
            )
        )

        rule.onNodeWithTag("overlap").assertWidthIsEqualTo(120.dp)
        rule.onNodeWithTag("fill").assertWidthIsEqualTo(120.dp)
    }

    @Test
    fun `gallery child defines the wrap_content height next to a match_parent sibling`() {
        setContent(
            overlap(
                width = fixed(200),
                height = wrapContent(),
                items = listOf(
                    gallery(height = wrapContent(), items = listOf(fixedBox(50, 40))),
                    container(id = "fill", width = fixed(10), height = matchParent()),
                ),
            )
        )

        rule.onNodeWithTag("overlap").assertHeightIsEqualTo(40.dp)
        rule.onNodeWithTag("fill").assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun `min_size of a match_parent child is resolved with the variables of the child`() {
        setContent(
            container(
                id = "overlap",
                width = wrapContent(),
                orientation = DivContainer.Orientation.OVERLAP,
                variables = listOf(variable("size", 40L)),
                items = listOf(
                    fixedBox(100, 40),
                    container(
                        id = "minimum",
                        width = matchParent(minSize = intExpression("@{size}")),
                        variables = listOf(variable("size", 160L)),
                    ),
                ),
            )
        )

        rule.onNodeWithTag("overlap").assertWidthIsEqualTo(160.dp)
        rule.onNodeWithTag("minimum").assertWidthIsEqualTo(160.dp)
    }

    @Test
    fun `match_parent child defines the wrap_content width when its sibling has no width`() {
        setContent(
            overlap(
                width = wrapContent(),
                height = fixed(50),
                items = listOf(
                    container(width = fixed(0), height = fixed(10)),
                    container(id = "fill", width = matchParent(), items = listOf(fixedBox(120, 10))),
                ),
            )
        )

        rule.onNodeWithTag("overlap").assertWidthIsEqualTo(120.dp)
        rule.onNodeWithTag("fill").assertWidthIsEqualTo(120.dp)
    }

    private fun overlap(width: DivSize, height: DivSize = wrapContent(), items: List<Div>): Div =
        container(
            id = "overlap",
            width = width,
            height = height,
            orientation = DivContainer.Orientation.OVERLAP,
            items = items,
        )

    private fun fixedBox(width: Int, height: Int): Div =
        container(width = fixed(width), height = fixed(height))

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }
}

/** A custom element whose content cannot answer intrinsic size queries. */
private class ConstrainedBoxFactory : DivCustomViewFactory {
    @Composable
    override fun Content(modifier: Modifier, environment: DivCustomEnvironment) {
        BoxWithConstraints(modifier) {
            Box(Modifier.size(width = minOf(maxWidth, 120.dp), height = 30.dp))
        }
    }
}
