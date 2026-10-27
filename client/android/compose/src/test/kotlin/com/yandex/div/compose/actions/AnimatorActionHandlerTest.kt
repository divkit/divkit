package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.animation.AnimatorStorage
import com.yandex.div.test.data.action
import com.yandex.div.test.data.animatorStartAction
import com.yandex.div.test.data.animatorStopAction
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionTyped
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class AnimatorActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()
    private val animatorStorage = mock<AnimatorStorage>()

    @BeforeTest
    fun setUp() {
        actionHandlerEnvironment.init(
            animatorActionHandler = AnimatorActionHandler(animatorStorage)
        )
    }

    @Test
    fun `start action starts animator in its scope`() {
        val typedAction = animatorStartAction(ANIMATOR_ID)

        handle(action(scopeId = SCOPE_ID, typed = typedAction))

        verify(animatorStorage).start(
            SCOPE_ID,
            (typedAction as DivActionTyped.AnimatorStart).value,
        )
    }

    @Test
    fun `stop action stops animator in its scope`() {
        handle(action(scopeId = SCOPE_ID, typed = animatorStopAction(ANIMATOR_ID)))

        verify(animatorStorage).stop(SCOPE_ID, ANIMATOR_ID)
    }

    @Test
    fun `animator action without scope is ignored`() {
        handle(action(typed = animatorStartAction(ANIMATOR_ID)))

        verifyNoInteractions(animatorStorage)
    }

    private fun handle(action: DivAction) = actionHandlerEnvironment.handle(action)

    private companion object {
        const val ANIMATOR_ID = "animator"
        const val SCOPE_ID = "scope"
    }
}
