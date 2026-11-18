package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.utils.applyIf
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class RestrictParentScrollTest {

    @get:Rule
    val rule = createComposeRule()

    private val parentScroll = ScrollState(0)
    private val childList = LazyListState()

    @Test
    fun `vertical drag on a restricted horizontal scrollable does not scroll the vertical parent`() {
        setContent(isParentHorizontal = false, isChildHorizontal = true, isRestricted = true)

        rule.onNodeWithTag(CHILD_TAG).performTouchInput { swipeUp() }
        rule.waitForIdle()

        assertEquals(0, parentScroll.value, "parent scroll offset")
    }

    @Test
    fun `vertical drag on an unrestricted horizontal scrollable scrolls the vertical parent`() {
        setContent(isParentHorizontal = false, isChildHorizontal = true, isRestricted = false)

        rule.onNodeWithTag(CHILD_TAG).performTouchInput { swipeUp() }
        rule.waitForIdle()

        assertTrue(parentScroll.value > 0, "parent scroll offset ${parentScroll.value} is not positive")
    }

    @Test
    fun `restricted horizontal scrollable still scrolls on a horizontal drag`() {
        setContent(isParentHorizontal = false, isChildHorizontal = true, isRestricted = true)

        rule.onNodeWithTag(CHILD_TAG).performTouchInput { swipeLeft() }
        rule.waitForIdle()

        assertTrue(childList.firstVisibleItemIndex > 0, "child first visible item ${childList.firstVisibleItemIndex}")
        assertEquals(0, parentScroll.value, "parent scroll offset")
    }

    @Test
    fun `own-axis leftover of a restricted horizontal scrollable does not scroll the horizontal parent`() {
        setContent(isParentHorizontal = true, isChildHorizontal = true, isRestricted = true, childItemCount = 1)

        rule.onNodeWithTag(CHILD_TAG).performTouchInput { swipeLeft() }
        rule.waitForIdle()

        assertEquals(0, parentScroll.value, "parent scroll offset")
    }

    @Test
    fun `own-axis leftover of an unrestricted horizontal scrollable scrolls the horizontal parent`() {
        setContent(isParentHorizontal = true, isChildHorizontal = true, isRestricted = false, childItemCount = 1)

        rule.onNodeWithTag(CHILD_TAG).performTouchInput { swipeLeft() }
        rule.waitForIdle()

        assertTrue(parentScroll.value > 0, "parent scroll offset ${parentScroll.value} is not positive")
    }

    @Test
    fun `horizontal drag on a restricted horizontal scrollable with scrolling disabled does not scroll the horizontal parent`() {
        setContent(
            isParentHorizontal = true,
            isChildHorizontal = true,
            isRestricted = true,
            isChildScrollEnabled = false,
        )

        rule.onNodeWithTag(CHILD_TAG).performTouchInput { swipeLeft() }
        rule.waitForIdle()

        assertEquals(0, parentScroll.value, "parent scroll offset")
    }

    @Test
    fun `cross-axis leftover of content nested in a restricted horizontal scrollable scrolls the vertical parent`() {
        setContent(
            isParentHorizontal = false,
            isChildHorizontal = true,
            isRestricted = true,
            childItemCount = 1,
            childItem = {
                LazyColumn(modifier = Modifier.size(ITEM_SIZE).testTag(NESTED_TAG)) {
                    items(count = 1) { Box(modifier = Modifier.size(ITEM_SIZE)) }
                }
            },
        )

        rule.onNodeWithTag(NESTED_TAG).performTouchInput { swipeUp() }
        rule.waitForIdle()

        assertTrue(parentScroll.value > 0, "parent scroll offset ${parentScroll.value} is not positive")
    }

    @Test
    fun `horizontal drag on a restricted vertical scrollable does not scroll the horizontal parent`() {
        setContent(isParentHorizontal = true, isChildHorizontal = false, isRestricted = true)

        rule.onNodeWithTag(CHILD_TAG).performTouchInput { swipeLeft() }
        rule.waitForIdle()

        assertEquals(0, parentScroll.value, "parent scroll offset")
    }

    @Test
    fun `own-axis leftover of a restricted vertical scrollable does not scroll the vertical parent`() {
        setContent(isParentHorizontal = false, isChildHorizontal = false, isRestricted = true, childItemCount = 1)

        rule.onNodeWithTag(CHILD_TAG).performTouchInput { swipeUp() }
        rule.waitForIdle()

        assertEquals(0, parentScroll.value, "parent scroll offset")
    }

    private fun setContent(
        isParentHorizontal: Boolean,
        isChildHorizontal: Boolean,
        isRestricted: Boolean,
        childItemCount: Int = 10,
        isChildScrollEnabled: Boolean = true,
        childItem: @Composable () -> Unit = { Box(modifier = Modifier.size(ITEM_SIZE)) },
    ) {
        rule.setContent {
            val child: @Composable () -> Unit = {
                val childModifier = Modifier
                    .size(VIEWPORT_SIZE)
                    .applyIf(isRestricted) { restrictParentScroll(isHorizontal = isChildHorizontal) }
                    .testTag(CHILD_TAG)
                if (isChildHorizontal) {
                    LazyRow(state = childList, modifier = childModifier, userScrollEnabled = isChildScrollEnabled) {
                        items(count = childItemCount) { childItem() }
                    }
                } else {
                    LazyColumn(state = childList, modifier = childModifier, userScrollEnabled = isChildScrollEnabled) {
                        items(count = childItemCount) { childItem() }
                    }
                }
            }
            if (isParentHorizontal) {
                Row(modifier = Modifier.size(VIEWPORT_SIZE).horizontalScroll(parentScroll)) {
                    child()
                    Spacer(modifier = Modifier.width(PARENT_FILLER))
                }
            } else {
                Column(modifier = Modifier.size(VIEWPORT_SIZE).verticalScroll(parentScroll)) {
                    child()
                    Spacer(modifier = Modifier.height(PARENT_FILLER))
                }
            }
        }
        rule.waitForIdle()
    }
}

private const val CHILD_TAG = "child"
private const val NESTED_TAG = "nested"
private val VIEWPORT_SIZE = 200.dp
private val ITEM_SIZE = 100.dp
private val PARENT_FILLER = 1000.dp
