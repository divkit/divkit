package com.yandex.div.core.tooltip

import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivTooltip
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class TooltipDataTest {

    private val runtimeStore = mock<RuntimeStore> {
        on { getOrCreateRuntime(any(), any(), any()) } doReturn ExpressionsRuntime(mock())
    }
    private val divView = mock<Div2View> {
        on { runtimeStore } doReturn runtimeStore
    }
    private val divTooltip = DivTooltip(
        div = text(text = "tooltip"),
        id = "tooltip_id",
        position = constant(DivTooltip.Position.BOTTOM),
        substrateDiv = text(text = "substrate"),
    )

    @Test
    fun `tooltip block path is scoped by tooltip id`() {
        val path = tooltipData().tooltipBlock.path

        assertEquals("tooltip#10#tooltip_id", path.namespace)
        assertEquals("tooltip#10#tooltip_id/0", path.statesString)
        assertEquals("0/tooltip#tooltip_id", path.fullPath)
    }

    @Test
    fun `tooltip block path is scoped by scope id`() {
        val path = tooltipData(scopeId = "scope_a").tooltipBlock.path

        assertEquals("tooltip#10#tooltip_id#7#scope_a/0", path.statesString)
    }

    @Test
    fun `tooltip without scope does not share path with scope 0`() {
        val unscoped = tooltipData().tooltipBlock.path
        val scoped = tooltipData(scopeId = "0").tooltipBlock.path

        assertNotEquals(unscoped.statesString, scoped.statesString)
    }

    @Test
    fun `tooltip id with separators does not share path with scoped tooltip`() {
        val unscoped = tooltipData(id = "a#1#b").tooltipBlock.path
        val scoped = tooltipData(id = "a", scopeId = "b").tooltipBlock.path

        assertNotEquals(unscoped.statesString, scoped.statesString)
    }

    @Test
    fun `substrate block path is scoped separately`() {
        val path = tooltipData().substrateBlock?.path

        assertEquals("tooltip_substrate#10#tooltip_id/0", path?.statesString)
    }

    @Test
    fun `tooltip block path keeps anchor states`() {
        val anchorPath = DivStatePath.fromState(0).append("outer", "first", "first")

        val path = tooltipData(anchorPath = anchorPath).tooltipBlock.path

        assertEquals("tooltip#10#tooltip_id/0/outer/first", path.statesString)
    }

    @Test
    fun `tooltip state does not collide with card state that mimics tooltip ids`() {
        val tooltipPath = tooltipData().tooltipBlock.path.appendDiv("state")
        val cardPaths = listOf(
            DivStatePath.fromState(0).append("tooltip#tooltip_id", "global", "global"),
            DivStatePath.fromState(0).append("tooltip#10#tooltip_id", "0", "0"),
        ).map { it.appendDiv("state") }

        cardPaths.forEach { cardPath ->
            val cardState = cardPath.append("state", "second", "second")
            val tooltipState = tooltipPath.append("state", "second", "second")
            assertNotEquals(cardState.pathToLastState, tooltipState.pathToLastState)
            assertFalse(tooltipData().tooltipBlock.path.isAncestorOf(cardState))
        }
    }

    @Test
    fun `tooltip block of anchor inside card state is not ancestor of card paths`() {
        val anchorPath = DivStatePath.fromState(0).append("outer", "first", "first")
        val tooltipPath = tooltipData(anchorPath = anchorPath).tooltipBlock.path

        assertFalse(tooltipPath.isAncestorOf(anchorPath.append("state", "second", "second")))
        assertTrue(tooltipPath.isAncestorOf(tooltipPath.append("state", "second", "second")))
    }

    private fun tooltipData(
        id: String = "tooltip_id",
        scopeId: String? = null,
        anchorPath: DivStatePath = DivStatePath.fromState(0),
    ) = TooltipData(
        id = id,
        scopeId = scopeId,
        divTooltip = DivTooltip(
            div = divTooltip.div,
            id = id,
            position = divTooltip.position,
            substrateDiv = divTooltip.substrateDiv,
        ),
        anchor = mock<View>(),
        anchorBlock = DivBlock.Text(mock<Div.Text>(), ExpressionResolver.EMPTY, anchorPath),
        divView = divView,
    )
}
