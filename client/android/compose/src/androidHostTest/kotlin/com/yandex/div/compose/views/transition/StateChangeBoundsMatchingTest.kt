package com.yandex.div.compose.views.transition

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class StateChangeBoundsMatchingTest {
    private val source = StateChangeBoundsStart(ElementBounds(0f, 0f, 40f, 40f), Offset.Zero, specs = null)
    private val matches = mutableListOf<Set<String>>()
    private val matching = StateChangeBoundsMatching(mapOf(ID to source)) { matches += it }
    private val state = StateChangeTransitionState(matching)

    @Test
    fun `a source is offered until the first placed tree settles the matches`() {
        state.registerRenderedId(ID)
        assertSame(source, matching.source(ID))

        state.finishBoundsMatching()

        assertEquals(listOf(setOf(ID)), matches)
        assertTrue(source.targetIsUnique)
        assertNull(matching.source(ID))
        state.finishBoundsMatching()
        assertEquals(1, matches.size)
    }

    @Test
    fun `a repeated incoming id is not matched`() {
        state.registerRenderedId(ID)
        state.registerRenderedId(ID)
        assertFalse(source.targetIsUnique)

        state.finishBoundsMatching()

        assertEquals(listOf(emptySet()), matches)
        assertFalse(source.targetIsUnique)
    }

    @Test
    fun `an id absent from the first placed tree is not matched`() {
        state.finishBoundsMatching()

        assertEquals(listOf(emptySet()), matches)
        assertFalse(source.targetIsUnique)
    }

    @Test
    fun `an element revealed after matching does not change the match`() {
        state.registerRenderedId(ID)
        state.finishBoundsMatching()

        state.registerRenderedId(ID)

        assertTrue(source.targetIsUnique)
    }

    @Test
    fun `deferred content keeps matching open until it resumes`() {
        matching.defer()
        state.finishBoundsMatching()
        assertSame(source, matching.source(ID))

        state.registerRenderedId(ID)
        matching.resume()
        state.finishBoundsMatching()

        assertEquals(listOf(setOf(ID)), matches)
    }

    private companion object {
        const val ID = "box"
    }
}
