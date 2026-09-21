package com.yandex.div.compose.actions

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.haptics.HapticFeedbackStorage
import com.yandex.div.test.data.action
import com.yandex.div.test.data.hapticAction
import com.yandex.div2.DivActionHaptic
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class HapticActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()
    private val hapticFeedbackStorage = HapticFeedbackStorage()
    private val hapticFeedback = mock<HapticFeedback>()

    @BeforeTest
    fun setUp() {
        hapticFeedbackStorage.register(hapticFeedback)
        actionHandlerEnvironment.init(
            hapticActionHandler = HapticActionHandler(
                hapticFeedbackStorage = hapticFeedbackStorage,
                reporter = actionHandlerEnvironment.reporter
            )
        )
    }

    @Test
    fun `light action performs frequent tick`() {
        handle(DivActionHaptic.Feedback.LIGHT)

        verify(hapticFeedback).performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    @Test
    fun `medium action performs context click`() {
        handle(DivActionHaptic.Feedback.MEDIUM)

        verify(hapticFeedback).performHapticFeedback(HapticFeedbackType.ContextClick)
    }

    @Test
    fun `heavy action performs long press`() {
        handle(DivActionHaptic.Feedback.HEAVY)

        verify(hapticFeedback).performHapticFeedback(HapticFeedbackType.LongPress)
    }

    @Test
    fun `success action performs confirm`() {
        handle(DivActionHaptic.Feedback.SUCCESS)

        verify(hapticFeedback).performHapticFeedback(HapticFeedbackType.Confirm)
    }

    @Test
    fun `error action performs reject`() {
        handle(DivActionHaptic.Feedback.ERROR)

        verify(hapticFeedback).performHapticFeedback(HapticFeedbackType.Reject)
    }

    private fun handle(feedback: DivActionHaptic.Feedback) {
        actionHandlerEnvironment.handle(action(typed = hapticAction(feedback)))
    }
}
