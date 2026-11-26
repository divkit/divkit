package com.yandex.div.compose.views.transition

import android.graphics.Bitmap
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.core.view.drawToBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivView
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.screenshot.ScreenshotTestConfiguration
import com.yandex.div.compose.setContentWithDivContext
import org.json.JSONObject
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w390dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DivStateArticleTransitionTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `article expands slides comments in and collapses`() {
        val file = File("../../../test_data/regression_test_data/animations/article_with_transitions.json")
        val data = ScreenshotTestConfiguration(file.name, JSONObject(file.readText())).parseDivData()
        lateinit var view: View
        rule.mainClock.autoAdvance = false
        rule.setContentWithDivContext(divConfiguration {}) {
            val currentView = LocalView.current
            SideEffect { view = currentView }
            Column(Modifier.width(360.dp).verticalScroll(rememberScrollState())) {
                DivView(data)
            }
        }
        fun advance(millis: Long) {
            rule.mainClock.advanceTimeBy(millis)
            rule.waitForIdle()
        }

        val text = rule.onNodeWithTag("content_text", useUnmergedTree = true)
        fun textHeight() = text.getUnclippedBoundsInRoot().let { it.bottom - it.top }
        advance(32)
        val collapsed = textHeight()
        rule.onNodeWithText("EXPAND").performClick()
        advance(112)
        val expanding = textHeight()
        advance(400)
        val expanded = textHeight()
        assertTrue(expanding > collapsed && expanding < expanded, "Expand: $collapsed / $expanding / $expanded")

        rule.onNodeWithText("SHOW COMMENTS").performClick()
        advance(112)
        val entering = rule.runOnIdle { view.drawToBitmap() }
        advance(400)
        val shown = rule.runOnIdle { view.drawToBitmap() }
        // An entering comment is drawn by its overlay, clipped by the comments, while its node already
        // has the final bounds. Measure the drawn span across the middle of each comment.
        fun drawnSpan(image: Bitmap, id: String): IntRange {
            val node = rule.onAllNodesWithTag(id, useUnmergedTree = true)[0].fetchSemanticsNode()
            val row = node.boundsInRoot.center.y.toInt()
            val drawn = (0 until image.width).filter { image.getPixel(it, row) != shown.getPixel(0, row) }
            return drawn.first()..drawn.last()
        }
        val fromLeft = drawnSpan(entering, "comment_01")
        val fromRight = drawnSpan(entering, "comment_02")
        val left = drawnSpan(shown, "comment_01")
        val right = drawnSpan(shown, "comment_02")
        assertTrue(fromLeft.last < left.last, "The first comment slides in from the left: $fromLeft / $left")
        assertTrue(fromRight.first > right.first, "The second comment slides in from the right: $fromRight / $right")

        rule.onNodeWithText("COLLAPSE").performClick()
        advance(112)
        val collapsing = textHeight()
        assertTrue(collapsing > collapsed && collapsing < expanded, "Collapse: $expanded / $collapsing / $collapsed")
        advance(400)
        assertEquals(collapsed, textHeight())
    }
}
