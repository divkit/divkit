package com.yandex.div.compose.views.pager

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.toSize
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivFocus
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

private const val PAGER_WIDTH = 300
private const val TOLERANCE = 0.5f

// Long enough for the first swipe to start settling, short enough to interrupt it.
private const val SETTLING_PART_MS = 32L

@RunWith(AndroidJUnit4::class)
class PagerSwipeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private lateinit var inputModeManager: InputModeManager

    @Test
    fun `fast swipe settles on the adjacent page`() {
        setPagerContent(pageWidth = PAGER_WIDTH)

        swipe()

        assertEquals(pagerBounds().left, itemBounds(1).left, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun `drag stops at the adjacent page`() {
        setPagerContent(pageWidth = PAGER_WIDTH / 2)
        val pager = pagerBounds()

        drag(-pager.width / 5)
        drag(-pager.width * 2)

        assertEquals(pager.left, itemBounds(1).left, absoluteTolerance = TOLERANCE)
        release()
    }

    @Test
    fun `dragging by the pager width turns one page`() {
        setPagerContent(pageWidth = PAGER_WIDTH / 2)
        val pager = pagerBounds()
        drag(-pager.width / 5)
        val start = itemBounds(0).left

        drag(-pager.width / 3)

        assertEquals(start - pager.width / 6, itemBounds(0).left, absoluteTolerance = TOLERANCE)
        release()
    }

    @Test
    fun `page keys turn one page per press`() {
        setPagerContent(pageWidth = PAGER_WIDTH, focusablePages = true)
        val pager = pagerBounds()
        // Focusable pages take focus only in the keyboard input mode, as with a hardware keyboard.
        composeRule.runOnIdle { inputModeManager.requestInputMode(InputMode.Keyboard) }
        composeRule.onNodeWithTag("item0").requestFocus()

        pressKey(Key.PageDown)
        pressKey(Key.PageDown)

        assertEquals(pager.left, itemBounds(2).left, absoluteTolerance = TOLERANCE)

        pressKey(Key.PageUp)

        assertEquals(pager.left, itemBounds(1).left, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun `swipe that interrupts settling selects every passed page in order`() {
        setPagerContent(pageWidth = PAGER_WIDTH)
        composeRule.mainClock.autoAdvance = false

        swipe()
        composeRule.mainClock.advanceTimeBy(SETTLING_PART_MS)
        swipe()
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()

        assertEquals(listOf("0", "1", "2"), selectedPages())
    }

    private fun setPagerContent(pageWidth: Int, focusablePages: Boolean = false) {
        val data = data(
            Div.Pager(
                value = DivPager(
                    height = fixed(100),
                    id = "pager",
                    items = List(5) { index ->
                        text(
                            height = fixed(100),
                            id = "item$index",
                            selectedActions = listOf(action(url = "divkit-test://select?page=$index")),
                            text = constant(index.toString()),
                            focus = if (focusablePages) DivFocus() else null,
                            width = fixed(pageWidth),
                        )
                    },
                    layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
                    scrollAxisAlignment = constant(DivPager.ItemAlignment.START),
                    width = fixed(PAGER_WIDTH),
                )
            )
        )
        val configuration = divConfiguration {
            actionHandler = this@PagerSwipeTest.actionHandler
            reporter = TestReporter()
        }
        composeRule.setContentWithDivContext(configuration) {
            inputModeManager = LocalInputModeManager.current
            DivView(data = data)
        }
    }

    private fun swipe() {
        composeRule.onNodeWithTag("pager").performTouchInput {
            swipeLeft(startX = right - 1f, endX = left + 1f, durationMillis = 50)
        }
    }

    private fun pressKey(key: Key) {
        composeRule.onNode(isFocused()).performKeyInput { pressKey(key) }
        composeRule.waitForIdle()
    }

    private fun drag(dx: Float) {
        composeRule.onNodeWithTag("pager").performTouchInput {
            if (currentPosition() == null) down(center)
            moveBy(Offset(dx, 0f))
        }
    }

    private fun release() {
        composeRule.onNodeWithTag("pager").performTouchInput { up() }
        composeRule.waitForIdle()
    }

    private fun selectedPages(): List<String?> = actionHandler.handledActions
        .filter { it.source == DivActionSource.SELECTION }
        .map { it.url?.getQueryParameter("page") }

    private fun pagerBounds(): Rect = composeRule.onNodeWithTag("pager").fetchSemanticsNode().boundsInRoot

    /** Unlike boundsInRoot, these bounds are not clipped by the pager. */
    private fun itemBounds(index: Int): Rect {
        val node = composeRule.onNodeWithTag("item$index", useUnmergedTree = true).fetchSemanticsNode()
        return Rect(node.positionInRoot, node.size.toSize())
    }
}
