package com.yandex.div.internal.core

import com.yandex.div.test.data.constant
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGrid
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivTooltip
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

/**
 * Elements built from one item builder prototype must not share a nested div: renderers keep
 * local state per div instance, so a shared child or tooltip would share its state too.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
class DivCopyTest(private val case: Case) {

    @Test
    fun `copy takes the given id and copies every nested div`() {
        val copy = case.prototype.copy(id = "copy")

        assertEquals("copy", copy.value().id)
        assertTrue(case.prototype.nestedDivs().isNotEmpty())
        assertNestedDivsCopied(case.prototype, copy)
    }

    private fun assertNestedDivsCopied(original: Div, copy: Div) {
        val originalChildren = original.nestedDivs()
        val copiedChildren = copy.nestedDivs()
        assertEquals(originalChildren.size, copiedChildren.size)
        originalChildren.zip(copiedChildren).forEach { (originalChild, copiedChild) ->
            assertNotSame(originalChild.value(), copiedChild.value())
            assertEquals(originalChild.value().id, copiedChild.value().id)
            assertNestedDivsCopied(originalChild, copiedChild)
        }
    }

    class Case(private val name: String, val prototype: Div) {
        override fun toString() = name
    }

    companion object {
        private const val ELEMENT_ID = "element"

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases(): List<Case> = cases

        // The runner calls the provider once per case, so the list is built once.
        private val cases: List<Case> = listOf(
            Case(
                "container",
                Div.Container(
                    DivContainer(id = ELEMENT_ID, items = children(), tooltips = tooltips())
                )
            ),
            Case(
                "custom",
                Div.Custom(
                    DivCustom(
                        customType = "custom",
                        id = ELEMENT_ID,
                        items = children(),
                        tooltips = tooltips()
                    )
                )
            ),
            Case(
                "gallery",
                Div.Gallery(DivGallery(id = ELEMENT_ID, items = children(), tooltips = tooltips()))
            ),
            Case(
                "grid",
                Div.Grid(
                    DivGrid(
                        columnCount = constant(1L),
                        id = ELEMENT_ID,
                        items = children(),
                        tooltips = tooltips()
                    )
                )
            ),
            Case(
                "pager",
                Div.Pager(
                    DivPager(
                        id = ELEMENT_ID,
                        items = children(),
                        layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
                        tooltips = tooltips()
                    )
                )
            ),
            Case(
                "state",
                Div.State(
                    DivState(
                        id = ELEMENT_ID,
                        states = children().mapIndexed { index, div ->
                            DivState.State(div = div, stateId = "$index")
                        },
                        tooltips = tooltips()
                    )
                )
            ),
            Case(
                "tabs",
                Div.Tabs(
                    DivTabs(
                        id = ELEMENT_ID,
                        items = children().map { DivTabs.Item(div = it, title = constant("tab")) },
                        tooltips = tooltips()
                    )
                )
            ),
            Case("text", text(id = ELEMENT_ID, text = "text", tooltips = tooltips())),
        )

        private fun children(): List<Div> = listOf(
            text(id = "child", text = "child", tooltips = tooltips()),
            Div.Container(
                DivContainer(id = "nested", items = listOf(text(id = "leaf", text = "leaf")))
            ),
            Div.State(
                DivState(
                    id = "nested_state",
                    states = listOf(DivState.State(div = text(text = "state"), stateId = "state"))
                )
            ),
        )

        private fun tooltips(): List<DivTooltip> = listOf(
            DivTooltip(
                div = text(id = "tooltip", text = "tooltip"),
                id = "tooltip",
                position = constant(DivTooltip.Position.BOTTOM),
                substrateDiv = text(id = "substrate", text = "substrate")
            )
        )
    }
}

/** The divs a renderer creates nodes for: children, tab and state contents, tooltips. */
private fun Div.nestedDivs(): List<Div> {
    val children = when (this) {
        is Div.Container -> value.items.orEmpty()
        is Div.Custom -> value.items.orEmpty()
        is Div.Gallery -> value.items.orEmpty()
        is Div.Grid -> value.items.orEmpty()
        is Div.Pager -> value.items.orEmpty()
        is Div.State -> value.states.mapNotNull { it.div }
        is Div.Tabs -> value.items.map { it.div }
        else -> emptyList()
    }
    val tooltips = value().tooltips.orEmpty().flatMap { listOfNotNull(it.div, it.substrateDiv) }
    return children + tooltips
}
