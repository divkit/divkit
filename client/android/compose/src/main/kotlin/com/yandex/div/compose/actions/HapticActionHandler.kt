package com.yandex.div.compose.actions

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.haptics.HapticFeedbackStorage
import com.yandex.div2.DivActionHaptic
import javax.inject.Inject

internal class HapticActionHandler @Inject constructor(
    private val hapticFeedbackStorage: HapticFeedbackStorage,
    private val reporter: DivReporter,
) {

    fun handle(context: DivActionHandlingContext, action: DivActionHaptic) {
        val feedback = action.feedback.evaluate(context.expressionResolver)
        val hapticFeedback = hapticFeedbackStorage.get()
        if (hapticFeedback == null) {
            reporter.reportError("No haptic feedback is bound for haptic action")
            return
        }

        hapticFeedback.performHapticFeedback(feedback.toHapticFeedbackType())
    }
}

private fun DivActionHaptic.Feedback.toHapticFeedbackType(): HapticFeedbackType = when (this) {
    DivActionHaptic.Feedback.LIGHT -> HapticFeedbackType.SegmentFrequentTick
    DivActionHaptic.Feedback.MEDIUM -> HapticFeedbackType.ContextClick
    DivActionHaptic.Feedback.HEAVY -> HapticFeedbackType.LongPress
    DivActionHaptic.Feedback.SUCCESS -> HapticFeedbackType.Confirm
    DivActionHaptic.Feedback.ERROR -> HapticFeedbackType.Reject
}
