package com.yandex.div.compose.views.pager

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.text
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class PagerNestedScrollTest {

    @get:Rule
    val rule = createComposeRule()

    private val configuration = DivConfiguration(reporter = TestReporter())
    private val parentScroll = ScrollState(0)

    @Test
    fun `unrestricted pager claims a horizontal drag before its horizontal parent`() {
        setPager(isParentHorizontal = true, isRestricted = false)

        rule.onNodeWithTag("pager").performTouchInput {
            swipeLeft(durationMillis = 1000)
        }
        rule.waitForIdle()

        rule.onNodeWithText("Second").assertIsDisplayed()
        assertEquals(0, parentScroll.value)
    }

    @Test
    fun `restricted pager still turns pages inside a horizontal parent`() {
        setPager(isParentHorizontal = true, isRestricted = true)

        rule.onNodeWithTag("pager").performTouchInput {
            swipeLeft(durationMillis = 1000)
        }
        rule.waitForIdle()

        rule.onNodeWithText("Second").assertIsDisplayed()
        assertEquals(0, parentScroll.value)
    }

    @Test
    fun `unrestricted horizontal pager passes vertical drags to its parent`() {
        setPager(isParentHorizontal = false, isRestricted = false)

        rule.onNodeWithTag("pager").performTouchInput {
            swipeUp()
        }
        rule.waitForIdle()

        assertTrue(parentScroll.value > 0)
    }

    @Test
    fun `restricted horizontal pager keeps vertical drags from its parent`() {
        setPager(isParentHorizontal = false, isRestricted = true)

        rule.onNodeWithTag("pager").performTouchInput {
            swipeUp()
        }
        rule.waitForIdle()

        assertEquals(0, parentScroll.value)
    }

    @Test
    fun `unrestricted pager passes edge overflow to its horizontal parent`() {
        setPager(isParentHorizontal = true, isRestricted = false, defaultItem = 1)

        rule.onNodeWithTag("pager").performTouchInput {
            swipeLeft()
        }
        rule.waitForIdle()

        assertTrue(parentScroll.value > 0)
    }

    @Test
    fun `restricted pager keeps edge overflow from its horizontal parent`() {
        setPager(isParentHorizontal = true, isRestricted = true, defaultItem = 1)

        rule.onNodeWithTag("pager").performTouchInput {
            swipeLeft()
        }
        rule.waitForIdle()

        assertEquals(0, parentScroll.value)
    }

    private fun setPager(
        isParentHorizontal: Boolean,
        isRestricted: Boolean,
        defaultItem: Long = 0
    ) {
        val data = data(
            pager(
                id = "pager",
                defaultItem = defaultItem,
                width = fixed(200),
                height = fixed(200),
                items = listOf(
                    text(text = constant("First"), width = fixed(200)),
                    text(text = constant("Second"), width = fixed(200)),
                ),
                restrictParentScroll = constant(isRestricted),
            )
        )
        rule.setContentWithDivContext(configuration) {
            if (isParentHorizontal) {
                Row(
                    modifier = Modifier
                        .size(200.dp)
                        .horizontalScroll(parentScroll)
                ) {
                    DivView(data)
                    Spacer(Modifier.width(1000.dp))
                }
            } else {
                Column(
                    modifier = Modifier
                        .size(200.dp)
                        .verticalScroll(parentScroll)
                ) {
                    DivView(data)
                    Spacer(Modifier.height(1000.dp))
                }
            }
        }
        rule.waitForIdle()
    }
}
