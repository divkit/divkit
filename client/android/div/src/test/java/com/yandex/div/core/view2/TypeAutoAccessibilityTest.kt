package com.yandex.div.core.view2

import android.app.Activity
import android.view.View
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.view2.divs.UnitTestData
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TypeAutoAccessibilityTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val imageLoader = mock<DivImageLoader> {
        on { loadImage(any(), any<DivImageDownloadCallback>()) } doReturn LoadReference { }
    }
    private val divContext = Div2Context(activity, DivConfiguration.Builder(imageLoader).build())
    private val data = UnitTestData("accessibility", "auto_types.json", "regression_test_data").dataWithTemplates
    private val divView: Div2View

    init {
        AccessibilityStateProvider.touchExplorationEnabled = true
        divView = Div2View(divContext)
        divView.setData(data, DivDataTag("tag"))
    }

    @Test
    fun `text is important for accessibility`() {
        checkViewIsImportantForAccessibility("text")
    }

    @Test
    fun `input is important for accessibility`() {
        checkViewIsImportantForAccessibility("input")
    }

    @Test
    fun `select is important for accessibility`() {
        checkViewIsImportantForAccessibility("select")
    }

    @Test
    fun `image with description is important for accessibility`() {
        checkViewIsImportantForAccessibility("image with description")
    }

    @Test
    fun `image without action or description is not important for accessibility`() {
        checkViewIsImportantForAccessibility("image", isImportant = false)
    }

    @Test
    fun `slider is important for accessibility`() {
        checkViewIsImportantForAccessibility("slider")
    }

    @Test
    fun `separator is not important for accessibility`() {
        checkViewIsImportantForAccessibility("separator", isImportant = false)
    }

    private fun checkViewIsImportantForAccessibility(tag: String, isImportant: Boolean = true) {
        Assert.assertEquals(isImportant, divView.findViewWithTag<View>(tag)?.isImportantForAccessibility)
    }
}
