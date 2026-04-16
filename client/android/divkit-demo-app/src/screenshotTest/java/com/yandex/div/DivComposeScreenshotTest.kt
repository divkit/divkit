package com.yandex.div

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.Div2ScreenshotTest.Companion.relativePath
import com.yandex.div.rule.composeScreenshotRule
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.DIV_SCREENSHOT_CASE_EXTENSION
import com.yandex.test.screenshot.Screenshot
import org.junit.Assert
import org.junit.Assume
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class DivComposeScreenshotTest(case: String, escapedCase: String) {

    private val activityRule = ActivityParamsTestRule(
        DivComposeScreenshotActivity::class.java,
        DivComposeScreenshotActivity.EXTRA_DIV_ASSET_NAME to case
    )

    @get:Rule
    val rule = composeScreenshotRule(case, activityRule, case.relativePath)

    @Screenshot(viewId = R.id.screenshot_view)
    @Test
    fun divScreenshot() {
        Assume.assumeTrue(
            "Skipping DivComposeScreenshotTest on API 24",
            Build.VERSION.SDK_INT > Build.VERSION_CODES.N
        )
    }

    companion object {
        private val context: Context = ApplicationProvider.getApplicationContext()

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            val suite = listOf(
                // div-separator
                "snapshot_test_data/div-separator",
                // div-text
                "snapshot_test_data/div-text/all_attributes.json",
                "snapshot_test_data/div-text/auto-ellipsize-by-max-lines.json",
                "snapshot_test_data/div-text/composite-background.json",
                "snapshot_test_data/div-text/corner-radius.json",
                "snapshot_test_data/div-text/corners_radius.json",
                "snapshot_test_data/div-text/corner-radius-clamp.json",
                "snapshot_test_data/div-text/border-with-stroke.json",
                "snapshot_test_data/div-text/custom-alpha.json",
                "snapshot_test_data/div-text/custom-height.json",
                "snapshot_test_data/div-text/custom-margins.json",
                "snapshot_test_data/div-text/custom-paddings.json",
                "snapshot_test_data/div-text/custom-width.json",
                "snapshot_test_data/div-text/custom-typo.json",
                "snapshot_test_data/div-text/display-text.json",
                "snapshot_test_data/div-text/empty.json",
                "snapshot_test_data/div-text/empty-text-height.json",
                "snapshot_test_data/div-text/fixed-size-with-paddings.json",
                "snapshot_test_data/div-text/font_weight.json",
                "snapshot_test_data/div-text/hyphenation.json",
                "snapshot_test_data/div-text/gradient-color-text.json",
                "snapshot_test_data/div-text/gradient-color-with-ranges.json",
                "snapshot_test_data/div-text/gradient-color-with-text-color-priority.json",
                "snapshot_test_data/div-text/image-background.json",
                "snapshot_test_data/div-text/multiline-text.json",
                "snapshot_test_data/div-text/one-line-text.json",
                "snapshot_test_data/div-text/right-text-alignment.json",
                "snapshot_test_data/div-text/single-line-height.json",
                "snapshot_test_data/div-text/small-line-height-for-font.json",
                "snapshot_test_data/div-text/solid-background.json",
                "snapshot_test_data/div-text/strike.json",
                "snapshot_test_data/div-text/shadow.json",
                "snapshot_test_data/div-text/text-shadow.json",
                "snapshot_test_data/div-text/text-shadow-alpha.json",
                "snapshot_test_data/div-text/text_range_font_family.json",
                "snapshot_test_data/div-text/truncate.json",
                "snapshot_test_data/div-text/variables-rendering.json",
                "snapshot_test_data/div-text/vertical-alignments.json",
                "snapshot_test_data/div-text/visibility-gone.json",
                "snapshot_test_data/div-text/visibility-invisible.json",
                "snapshot_test_data/div-text/underline.json",
                "snapshot_test_data/div-text/gradient-background.json",
                "snapshot_test_data/div-text/diagonal-gradient-background.json",
                // div-container
                "snapshot_test_data/div-container",
                "snapshot_test_data/div-container/constraint-propagation",
                // div-image
                "snapshot_test_data/div-image/scale_fill.json",
                "snapshot_test_data/div-image/scale_fill_bottom.json",
                "snapshot_test_data/div-image/scale_fill_right.json",
                "snapshot_test_data/div-image/scale_fit.json",
                "snapshot_test_data/div-image/scale_fit_bottom.json",
                "snapshot_test_data/div-image/scale_fit_left.json",
                "snapshot_test_data/div-image/scale_fit_right.json",
                "snapshot_test_data/div-image/scale_fit_top.json",
                "snapshot_test_data/div-image/scale_stretch.json",
                "snapshot_test_data/div-image/no_scale.json",
                "snapshot_test_data/div-image/no_scale_bottom_right.json",
                "snapshot_test_data/div-image/no_scale_top_left.json",
                "snapshot_test_data/div-image/content-horizontal-alignment-end.json",
                "snapshot_test_data/div-image/content-horizontal-alignment-start.json",
                "snapshot_test_data/div-image/placeholder-color.json",
                "snapshot_test_data/div-image/custom-tint-color.json",
                "snapshot_test_data/div-image/custom-alpha.json",
                "snapshot_test_data/div-image/custom-height.json",
                "snapshot_test_data/div-image/custom-width.json",
                "snapshot_test_data/div-image/custom-margins.json",
                "snapshot_test_data/div-image/custom-paddings.json",
                "snapshot_test_data/div-image/corner-radius.json",
                "snapshot_test_data/div-image/corners_radius.json",
                "snapshot_test_data/div-image/border-with-stroke.json",
                "snapshot_test_data/div-image/blur.json",
                "snapshot_test_data/div-image/blur-with-big-radius.json",
                "snapshot_test_data/div-image/preview.json",
                // div-gallery
                "snapshot_test_data/div-gallery/corners_radius.json",
                "snapshot_test_data/div-gallery/empty.json",
                "snapshot_test_data/div-gallery/empty-gallery-wrap-content-with-paddings.json",
                "snapshot_test_data/div-gallery/gallery-default-item-position.json",
                "snapshot_test_data/div-gallery/gallery-default-item-with-states.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-content-alignment.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-custom-item-spacing.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-custom-margins.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-custom-paddings.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-default-item.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-fixed-height.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-fixed-width.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-horizontal-shrinking.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-items-not-fit.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-items-resizable-height.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-resizable-height.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-resizable-width.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-vertical-shrinking.json",
                "snapshot_test_data/div-gallery/horizontal-gallery-wrap-content-height-with-constraints.json",
                "snapshot_test_data/div-gallery/horizontal-grid-gallery-wrapped-item-spacing.json",
                "snapshot_test_data/div-gallery/vertical-gallery-content-alignment.json",
                "snapshot_test_data/div-gallery/vertical-gallery-custom-item-spacing.json",
                "snapshot_test_data/div-gallery/vertical-gallery-custom-margins.json",
                "snapshot_test_data/div-gallery/vertical-gallery-custom-paddings.json",
                "snapshot_test_data/div-gallery/vertical-gallery-fixed-height.json",
                "snapshot_test_data/div-gallery/vertical-gallery-fixed-width.json",
                "snapshot_test_data/div-gallery/vertical-gallery-height-wrap-content.json",
                "snapshot_test_data/div-gallery/vertical-gallery-horizontal-shrinking.json",
                "snapshot_test_data/div-gallery/vertical-gallery-items-not-fit.json",
                "snapshot_test_data/div-gallery/vertical-gallery-resizable-height.json",
                "snapshot_test_data/div-gallery/vertical-gallery-resizable-width.json",
                "snapshot_test_data/div-gallery/vertical-gallery-wrap-content-width-with-constraints.json",
                // div-pager
                "snapshot_test_data/div-pager/corners_radius.json",
                "snapshot_test_data/div-pager/empty.json",
                "snapshot_test_data/div-pager/horizontal-pager-constrained-height-with-paddings.json",
                "snapshot_test_data/div-pager/horizontal-pager-custom-neighbour-page-width.json",
                "snapshot_test_data/div-pager/horizontal-pager-custom-neighbour-page-width-with-paddings.json",
                "snapshot_test_data/div-pager/horizontal-pager-custom-page-width.json",
                "snapshot_test_data/div-pager/horizontal-pager-custom-page-width-with-paddings.json",
                "snapshot_test_data/div-pager/horizontal-pager-fixed-height.json",
                "snapshot_test_data/div-pager/horizontal-pager-fixed-height-with-paddings.json",
                "snapshot_test_data/div-pager/horizontal-pager-fixed-width.json",
                "snapshot_test_data/div-pager/horizontal-pager-items-resizable-height.json",
                "snapshot_test_data/div-pager/horizontal-pager-match-parent-height-with-constraints.json",
                "snapshot_test_data/div-pager/horizontal-pager-match-parent-height-with-paddings.json",
                "snapshot_test_data/div-pager/horizontal-pager-neighbour-page-size-mode-alignment-end.json",
                "snapshot_test_data/div-pager/horizontal-pager-neighbour-page-size-mode-alignment-start.json",
                "snapshot_test_data/div-pager/horizontal-pager-neighbour-page-size-mode-cross-axis-alignment.json",
                "snapshot_test_data/div-pager/horizontal-pager-neighbour-page-size-mode-few-pages.json",
                "snapshot_test_data/div-pager/horizontal-pager-neighbour-page-size-mode-few-pages-end.json",
                "snapshot_test_data/div-pager/horizontal-pager-neighbour-page-size-mode-item-spacing.json",
                "snapshot_test_data/div-pager/horizontal-pager-neighbour-page-size-mode-two-pages.json",
                "snapshot_test_data/div-pager/horizontal-pager-neighbour-page-width-0-with-item-spacing.json",
                "snapshot_test_data/div-pager/horizontal-pager-neighbour-page-width-0-with-paddings.json",
                "snapshot_test_data/div-pager/horizontal-pager-neighbour-page-width-bigger-than-page.json",
                "snapshot_test_data/div-pager/horizontal-pager-page-size-mode-alignment-end.json",
                "snapshot_test_data/div-pager/horizontal-pager-page-size-mode-alignment-start.json",
                "snapshot_test_data/div-pager/horizontal-pager-page-size-mode-cross-axis-alignment.json",
                "snapshot_test_data/div-pager/horizontal-pager-page-size-mode-few-pages.json",
                "snapshot_test_data/div-pager/horizontal-pager-page-size-mode-few-pages-end.json",
                "snapshot_test_data/div-pager/horizontal-pager-page-size-mode-item-spacing.json",
                "snapshot_test_data/div-pager/horizontal-pager-page-width-100-with-item-spacing.json",
                "snapshot_test_data/div-pager/horizontal-pager-page-width-100-with-paddings.json",
                "snapshot_test_data/div-pager/horizontal-pager-page-width-small.json",
                "snapshot_test_data/div-pager/horizontal-pager-resizable-height.json",
                "snapshot_test_data/div-pager/horizontal-pager-resizable-width.json",
                "snapshot_test_data/div-pager/horizontal-pager-same-paddings.json",
                "snapshot_test_data/div-pager/horizontal-pager-wrap-content-child-height-exceeds-parent.json",
                "snapshot_test_data/div-pager/horizontal-pager-wrap-content-height-with-different-children.json",
                "snapshot_test_data/div-pager/horizontal-pager-wrap-content-height-with-paddings.json",
                "snapshot_test_data/div-pager/horizontal-pager-wrap-content-size-mode-alignment-start.json",
                "snapshot_test_data/div-pager/horizontal-pager-wrap-content-size-mode-cross-axis-alignment.json",
                "snapshot_test_data/div-pager/horizontal-pager-wrap-content-size-mode-few-pages.json",
                "snapshot_test_data/div-pager/horizontal-pager-wrap-content-size-mode-few-pages-end.json",
                "snapshot_test_data/div-pager/horizontal-pager-wrap-content-size-mode-with-item-spacing.json",
                "snapshot_test_data/div-pager/horizontal-pager-wrap-content-width.json",
                "snapshot_test_data/div-pager/item-shadow-in-pager.json",
                "snapshot_test_data/div-pager/pager-default-item-with-states.json",
                "snapshot_test_data/div-pager/vertical-pager-constrained-width-with-paddings.json",
                "snapshot_test_data/div-pager/vertical-pager-custom-margins.json",
                "snapshot_test_data/div-pager/vertical-pager-custom-neighbour-page-width.json",
                "snapshot_test_data/div-pager/vertical-pager-custom-neighbour-page-width-with-paddings.json",
                "snapshot_test_data/div-pager/vertical-pager-custom-page-width.json",
                "snapshot_test_data/div-pager/vertical-pager-custom-page-width-with-paddings.json",
                "snapshot_test_data/div-pager/vertical-pager-fixed-height.json",
                "snapshot_test_data/div-pager/vertical-pager-fixed-width.json",
                "snapshot_test_data/div-pager/vertical-pager-fixed-width-with-paddings.json",
                "snapshot_test_data/div-pager/vertical-pager-match-parent-width-with-paddings.json",
                "snapshot_test_data/div-pager/vertical-pager-neighbour-page-size-mode-alignment-end.json",
                "snapshot_test_data/div-pager/vertical-pager-neighbour-page-size-mode-alignment-start.json",
                "snapshot_test_data/div-pager/vertical-pager-neighbour-page-size-mode-cross-axis-alignment.json",
                "snapshot_test_data/div-pager/vertical-pager-neighbour-page-size-mode-few-pages.json",
                "snapshot_test_data/div-pager/vertical-pager-neighbour-page-size-mode-few-pages-end.json",
                "snapshot_test_data/div-pager/vertical-pager-neighbour-page-size-mode-item-spacing.json",
                "snapshot_test_data/div-pager/vertical-pager-neighbour-page-size-mode-two-pages.json",
                "snapshot_test_data/div-pager/vertical-pager-neighbour-page-width-0-with-item-spacing.json",
                "snapshot_test_data/div-pager/vertical-pager-neighbour-page-width-0-with-paddings.json",
                "snapshot_test_data/div-pager/vertical-pager-neighbour-page-width-bigger-than-page.json",
                "snapshot_test_data/div-pager/vertical-pager-page-size-mode-alignment-end.json",
                "snapshot_test_data/div-pager/vertical-pager-page-size-mode-alignment-start.json",
                "snapshot_test_data/div-pager/vertical-pager-page-size-mode-cross-axis-alignment.json",
                "snapshot_test_data/div-pager/vertical-pager-page-size-mode-few-pages.json",
                "snapshot_test_data/div-pager/vertical-pager-page-size-mode-few-pages-end.json",
                "snapshot_test_data/div-pager/vertical-pager-page-size-mode-item-spacing.json",
                "snapshot_test_data/div-pager/vertical-pager-page-width-100-with-item-spacing.json",
                "snapshot_test_data/div-pager/vertical-pager-page-width-100-with-paddings.json",
                "snapshot_test_data/div-pager/vertical-pager-page-width-small.json",
                "snapshot_test_data/div-pager/vertical-pager-resizable-height.json",
                "snapshot_test_data/div-pager/vertical-pager-resizable-width.json",
                "snapshot_test_data/div-pager/vertical-pager-wrap-content-child-width-exceeds-parent.json",
                "snapshot_test_data/div-pager/vertical-pager-wrap-content-height.json",
                "snapshot_test_data/div-pager/vertical-pager-wrap-content-size-match-parent.json",
                "snapshot_test_data/div-pager/vertical-pager-wrap-content-size-mode-alignment-start.json",
                "snapshot_test_data/div-pager/vertical-pager-wrap-content-size-mode-cross-axis-alignment.json",
                "snapshot_test_data/div-pager/vertical-pager-wrap-content-size-mode-few-pages.json",
                "snapshot_test_data/div-pager/vertical-pager-wrap-content-size-mode-few-pages-end.json",
                "snapshot_test_data/div-pager/vertical-pager-wrap-content-size-mode-with-item-spacing.json",
                "snapshot_test_data/div-pager/vertical-pager-wrap-content-width-with-different-children.json",
                "snapshot_test_data/div-pager/vertical-pager-wrap-content-width-with-paddings.json",
                // div-background
                "snapshot_test_data/div-background/gradient-angles.json",
                "snapshot_test_data/div-background/gradient-positions.json",
                "snapshot_test_data/div-background/radial-gradient-positions.json",
                "snapshot_test_data/div-background/radial-positions.json",
                "snapshot_test_data/div-background/scale_stretch.json",
                "snapshot_test_data/div-background/blur.json",
                "snapshot_test_data/div-background/logical-position.json",
                // div-radial-gradient
                "snapshot_test_data/div-radial-gradient",
                // div-transform
                "snapshot_test_data/div-transform",
                // div-state
                "snapshot_test_data/div-states",
            ).expandDirectories()

            //TODO: to be stabilized
            val flakyTests = listOf(
                "snapshot_test_data/div-container/horizontal-orientation-bottom-horizontal-alignment.json",
                "snapshot_test_data/div-container/horizontal-orientation-space-around-alignment.json",
                "snapshot_test_data/div-container/horizontal-orientation-space-evenly-alignment.json",
                "snapshot_test_data/div-container/size_unit.json",
            ).expandDirectories().toSet()

            val resultSuite = (suite - flakyTests)
            Assert.assertEquals("Looks like there are outdated flaky tests",
                resultSuite.size, suite.size - flakyTests.size)

            return resultSuite.withEscapedParameter()
        }

        private fun List<String>.expandDirectories(): List<String> {
            return flatMap { path ->
                if (path.endsWith(DIV_SCREENSHOT_CASE_EXTENSION)) {
                    listOf(path)
                } else {
                    AssetEnumerator(context).enumerate(path, predicate = { filename ->
                        filename.endsWith(DIV_SCREENSHOT_CASE_EXTENSION)
                    }, recursive = false)
                }
            }
        }
    }
}
