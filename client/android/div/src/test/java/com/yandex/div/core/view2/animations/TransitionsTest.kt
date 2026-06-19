package com.yandex.div.core.view2.animations

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivChangeTransition
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TransitionsTest {

    @Test
    fun `inherited change applied to id-bearing node without own change`() {
        val inherited = changeBounds(999)

        val result = textWithId.toTransitionData(isIncoming = false, inheritedChange = inherited) { true }

        assertEquals("child", result?.viewId)
        val change = result!!.transitions.filterIsInstance<DivTransition.Change>().single()
        assertSame(inherited, change.value)
    }

    @Test
    fun `own change wins over inherited change`() {
        val inherited = changeBounds(999)
        val div = parse("""{"type":"text","text":"x","id":"child",
            "transition_change":{"type":"change_bounds","duration":100}}""")

        val result = div.item().toTransitionData(isIncoming = false, inheritedChange = inherited) { true }

        val change = result!!.transitions.filterIsInstance<DivTransition.Change>().single()
        assertSame(div.value().transitionChange, change.value)
        assertNotSame(inherited, change.value)
    }

    @Test
    fun `no own change and no inherited change yields null`() {
        val result = textWithId.toTransitionData(isIncoming = false, inheritedChange = null) { true }

        assertNull(result)
    }

    @Test
    fun `node without id is skipped even with inherited change`() {
        val result = parse("""{"type":"text","text":"x"}""").item()
            .toTransitionData(isIncoming = false, inheritedChange = changeBounds(999)) { true }

        assertNull(result)
    }

    @Test
    fun `incoming pass ignores inherited change`() {
        val div = parse("""{"type":"text","text":"x","id":"child","transition_in":{"type":"fade"}}""")

        val result = div.item().toTransitionData(isIncoming = true, inheritedChange = changeBounds(999)) { true }

        val transitions = result!!.transitions
        assertTrue(transitions.none { it is DivTransition.Change })
        assertTrue(transitions.single() is DivTransition.Appearance)
    }

    @Test
    fun `incoming pass without transition_in yields null even with inherited change`() {
        val result = textWithId.toTransitionData(isIncoming = true, inheritedChange = changeBounds(999)) { true }

        assertNull(result)
    }

    @Test
    fun `disallowed transitions yield null`() {
        val result = textWithId.toTransitionData(isIncoming = false, inheritedChange = changeBounds(999)) { false }

        assertNull(result)
    }

    private val textWithId: DivItemBuilderResult
        get() = parse("""{"type":"text","text":"x","id":"child"}""").item()

    private fun changeBounds(durationMs: Long): DivChangeTransition {
        return parse("""{"type":"text","text":"x",
            "transition_change":{"type":"change_bounds","duration":$durationMs}}""")
            .value().transitionChange!!
    }

    private fun Div.item() = DivItemBuilderResult(this, ExpressionResolver.EMPTY, DivStatePath.fromState(0))

    private fun parse(json: String): Div {
        val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)
        return Div(environment, JSONObject(json))
    }
}
