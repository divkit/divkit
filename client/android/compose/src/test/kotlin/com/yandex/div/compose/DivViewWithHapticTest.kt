package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.action
import com.yandex.div.test.data.data
import com.yandex.div.test.data.hapticAction
import com.yandex.div.test.data.text
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewWithHapticTest {
    @get:Rule
    val rule = createComposeRule()

    private val hapticFeedback = mock<HapticFeedback>()
    private val currentFeedback = mutableStateOf(hapticFeedback)
    private val isVisible = mutableStateOf(true)
    private val reporter = TestReporter()
    private val card = data(text(text = "button", action = action(typed = hapticAction())))
    private val divContext by lazy {
        DivContext(
            baseContext = RuntimeEnvironment.getApplication(),
            configuration = DivConfiguration(reporter = reporter)
        )
    }

    @Test
    fun `action performs haptic feedback`() {
        setContent()

        rule.onNodeWithText("button").performClick()

        verify(hapticFeedback).performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    @Test
    fun `action after leaving composition reports error without haptic feedback`() {
        reporter.failOnError = false
        setContent()
        rule.runOnIdle { isVisible.value = false }

        rule.runOnIdle {
            divContext.debugFeatures.performAction(card, action(typed = hapticAction()))
        }

        verifyNoInteractions(hapticFeedback)
        assertEquals(listOf("No haptic feedback is bound for haptic action"), reporter.errors)
    }

    @Test
    fun `action uses the new LocalHapticFeedback`() {
        val newFeedback = mock<HapticFeedback>()
        setContent()
        rule.runOnIdle { currentFeedback.value = newFeedback }

        rule.onNodeWithText("button").performClick()

        verify(newFeedback).performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        verifyNoInteractions(hapticFeedback)
    }

    private fun setContent() {
        rule.setContent {
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalHapticFeedback provides currentFeedback.value
            ) {
                if (isVisible.value) {
                    DivView(card)
                }
            }
        }
    }
}
