package com.yandex.div.compose.views.state

import com.yandex.div.compose.views.transition.containsStateChangeTransitions
import com.yandex.div.test.data.container
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivFadeTransition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StateChangeContentTest {

    @Test
    fun `rapid retarget makes old exiting id common`() {
        val first = state(div = text(text = FIRST_STATE, id = SHARED_ID), stateId = FIRST_STATE)
        val middle = state(div = text(text = MIDDLE_STATE, id = MIDDLE_ID), stateId = MIDDLE_STATE)
        val target = state(div = text(text = TARGET_STATE, id = SHARED_ID), stateId = TARGET_STATE)
        val firstEntry = StateChangeContentEntry(first, initiallyVisible = true).apply {
            transitionState.registerRenderedId(SHARED_ID)
        }
        val entries = mutableListOf(firstEntry)

        entries.updateActiveState(middle, stateTransitionIds = ::rootId)
        val retainedEntry = entries.first { it.state.stateId == FIRST_STATE }
        assertEquals(setOf(SHARED_ID), retainedEntry.exitingIds)
        entries.first { it.contentActive }.transitionState.registerRenderedId(MIDDLE_ID)

        entries.updateActiveState(target, stateTransitionIds = ::rootId)

        assertEquals(emptySet(), retainedEntry.exitingIds)
        assertEquals(setOf(SHARED_ID), retainedEntry.commonIds)
    }

    @Test
    fun `plain states use regular content`() {
        val initial = state(div = text(text = FIRST_STATE, id = SHARED_ID), stateId = FIRST_STATE)
        val target = state(div = text(text = TARGET_STATE, id = MIDDLE_ID), stateId = TARGET_STATE)
        val contentState = StateChangeContentState(initial)

        val useTransitionContent = contentState.shouldUseTransitionContent(
            activeState = target,
            transitionsAvailable = true,
            activeStateHasTransitions = false,
            committedStateHasTransitions = false,
        )

        assertFalse(useTransitionContent)
    }

    @Test
    fun `outgoing transition keeps transition content for plain target state`() {
        val initial = state(div = text(text = FIRST_STATE, id = SHARED_ID), stateId = FIRST_STATE)
        val target = state(div = text(text = TARGET_STATE, id = MIDDLE_ID), stateId = TARGET_STATE)
        val contentState = StateChangeContentState(initial)

        val useTransitionContent = contentState.shouldUseTransitionContent(
            activeState = target,
            transitionsAvailable = true,
            activeStateHasTransitions = false,
            committedStateHasTransitions = true,
        )

        assertTrue(useTransitionContent)
    }

    @Test
    fun `transition in descendant is detected`() {
        val div = container(
            items = listOf(
                text(
                    text = TARGET_STATE,
                    transitionIn = DivAppearanceTransition.Fade(DivFadeTransition()),
                )
            )
        )

        assertTrue(div.containsStateChangeTransitions())
    }

    private fun rootId(div: Div?): Set<String> = setOfNotNull(div?.value()?.id)

    private companion object {
        const val FIRST_STATE = "first"
        const val MIDDLE_STATE = "middle"
        const val TARGET_STATE = "target"
        const val SHARED_ID = "shared"
        const val MIDDLE_ID = "middle_id"
    }
}
