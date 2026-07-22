package com.yandex.div

import android.os.Build
import com.yandex.div.Div2ScreenshotTest.Companion.relativePath
import com.yandex.div.rule.composeScreenshotRule
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivComposeScreenshotActivity
import com.yandex.test.rules.ActivityParamsTestRule
import com.yandex.test.screenshot.Screenshot
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

        @JvmStatic
        @Parameters(name = "{1}")
        fun cases(): List<Array<String>> {
            return AssetEnumerator()
                .enumerate("snapshot_test_data")
                .filter { !ignoredFiles.contains(it) }
                .withEscapedParameter()
        }
    }
}

private val ignoredFiles = listOf(
    // div-nine-patch-background not supported
    "snapshot_test_data/div-background/nine-patch-rhombs.json",
    "snapshot_test_data/div-background/nine-patch-rhombs-horizontal-insets.json",
    "snapshot_test_data/div-background/nine-patch-rhombs-large-all-insets.json",
    "snapshot_test_data/div-background/nine-patch-rhombs-large-bottom-inset.json",
    "snapshot_test_data/div-background/nine-patch-rhombs-large-bottom-left-inset.json",
    "snapshot_test_data/div-background/nine-patch-rhombs-large-bottom-left-right-inset.json",
    "snapshot_test_data/div-background/nine-patch-rhombs-vertical-insets.json",
    "snapshot_test_data/div-background/nine-patch-shape.json",

    // div-container.aspect not fully supported
    "snapshot_test_data/div-container/aspect/wrap_content-width-horizontal.json",
    "snapshot_test_data/div-container/aspect/wrap_content-width-overlap.json",
    "snapshot_test_data/div-container/aspect/wrap_content-width-overlap-fixed-height.json",
    "snapshot_test_data/div-container/aspect/wrap_content-width-overlap-match_parent-height.json",
    "snapshot_test_data/div-container/aspect/wrap_content-width-vertical.json",
    "snapshot_test_data/div-container/aspect/wrap_content-width-vertical-constrained-child-not-fit.json",
    "snapshot_test_data/div-container/aspect/wrap_content-width-vertical-fixed-height.json",
    "snapshot_test_data/div-container/aspect/wrap_content-width-vertical-match_parent-child-fits.json",
    "snapshot_test_data/div-container/aspect/wrap_content-width-vertical-match_parent-child-not-fit.json",
    "snapshot_test_data/div-container/aspect/wrap_content-width-vertical-match_parent-height.json",

    // div-container.item_builder not supported
    "snapshot_test_data/div-container/item_builder/index.json",
    "snapshot_test_data/div-container/item_builder/item-builder.json",
    "snapshot_test_data/div-container/item_builder/item-builder-priority.json",
    "snapshot_test_data/div-container/item_builder/item-builder-with-local-variables.json",
    "snapshot_test_data/div-container/item_builder/item-builder-with-nested-local-variables.json",
    "snapshot_test_data/div-container/item_builder/nested-builders.json",
    "snapshot_test_data/div-container/item_builder/non-unique-matched-selectors.json",

    // div-image.aspect not supported
    "snapshot_test_data/div-image/aspect-wrap_content.json",
    "snapshot_test_data/div-image/wrap-content-aspect-zero-constraints.json",
    "snapshot_test_data/div-image/wrap-content-image-with-aspect-in-horizontal-match-parent-container.json",
    "snapshot_test_data/div-image/wrap-content-image-with-aspect-in-horizontal-wrap-content-container.json",
    "snapshot_test_data/div-image/wrap-content-image-with-aspect-in-overlap-match-parent-container.json",
    "snapshot_test_data/div-image/wrap-content-image-with-aspect-in-overlap-wrap-content-container.json",
    "snapshot_test_data/div-image/wrap-content-image-with-aspect-in-vertical-match-parent-container.json",
    "snapshot_test_data/div-image/wrap-content-image-with-aspect-in-vertical-wrap-content-container.json",

    // div-gallery.scroll_content_alignment not supported
    "snapshot_test_data/div-gallery/default-item/horizontal-gallery-alignment-center.json",
    "snapshot_test_data/div-gallery/default-item/horizontal-gallery-alignment-center-rtl.json",
    "snapshot_test_data/div-gallery/default-item/horizontal-gallery-alignment-end.json",
    "snapshot_test_data/div-gallery/default-item/horizontal-gallery-alignment-end-rtl.json",
    "snapshot_test_data/div-gallery/default-item/horizontal-gallery-alignment-start.json",
    "snapshot_test_data/div-gallery/default-item/horizontal-gallery-alignment-start-rtl.json",
    "snapshot_test_data/div-gallery/default-item/horizontal-grid-gallery-alignment-center.json",
    "snapshot_test_data/div-gallery/default-item/horizontal-grid-gallery-alignment-center-rtl.json",
    "snapshot_test_data/div-gallery/default-item/horizontal-grid-gallery-alignment-end.json",
    "snapshot_test_data/div-gallery/default-item/horizontal-grid-gallery-alignment-end-rtl.json",
    "snapshot_test_data/div-gallery/default-item/horizontal-grid-gallery-alignment-start.json",
    "snapshot_test_data/div-gallery/default-item/horizontal-grid-gallery-alignment-start-rtl.json",
    "snapshot_test_data/div-gallery/default-item/vertical-gallery-alignment-center.json",
    "snapshot_test_data/div-gallery/default-item/vertical-gallery-alignment-end.json",
    "snapshot_test_data/div-gallery/default-item/vertical-gallery-alignment-start.json",
    "snapshot_test_data/div-gallery/default-item/vertical-gallery-alignment-start-rtl.json",
    "snapshot_test_data/div-gallery/default-item/vertical-grid-gallery-alignment-center.json",
    "snapshot_test_data/div-gallery/default-item/vertical-grid-gallery-alignment-end.json",
    "snapshot_test_data/div-gallery/default-item/vertical-grid-gallery-alignment-start.json",
    "snapshot_test_data/div-gallery/default-item/vertical-grid-gallery-alignment-start-rtl.json",

    // div-gallery.item_builder not supported
    "snapshot_test_data/div-gallery/item-builder/item-builder.json",
    "snapshot_test_data/div-gallery/item-builder/item-builder-with-local-variables.json",
    "snapshot_test_data/div-gallery/item-builder/nested-builders.json",
    "snapshot_test_data/div-gallery/item-builder/non-unique-matched-selectors.json",

    // div-base.layout_provider not supported
    "snapshot_test_data/div-layout-provider/layout-provider.json",

    // div-pager.item_builder not supported
    "snapshot_test_data/div-pager/item-builder/item-builder.json",
    "snapshot_test_data/div-pager/item-builder/item-builder-with-local-variables.json",
    "snapshot_test_data/div-pager/item-builder/nested-builders.json",
    "snapshot_test_data/div-pager/item-builder/non-unique-matched-selectors.json",

    // div-text.images not supported
    "snapshot_test_data/div-text/line-height/multi-line-text-with-image.json",
    "snapshot_test_data/div-text/line-height/single-line-text-with-image.json",
    "snapshot_test_data/div-text/custom-image-tint-color.json",
    "snapshot_test_data/div-text/custom-text-alignment-with-attachments.json",
    "snapshot_test_data/div-text/ellipsis-with-image.json",
    "snapshot_test_data/div-text/ellipsis_builder.json",
    "snapshot_test_data/div-text/image-reverse-indexing.json",
    "snapshot_test_data/div-text/image-tint-mode.json",
    "snapshot_test_data/div-text/image_builder.json",
    "snapshot_test_data/div-text/images.json",
    "snapshot_test_data/div-text/images_hyphenation.json",
    "snapshot_test_data/div-text/text_image_vertical_alignment.json",

    // div-text.range.mask not supported
    "snapshot_test_data/div-text/mask.json",

    // markdown extension not supported
    "snapshot_test_data/div-text/markdown-extension.json",

    // div-text.tighten_width not supported
    "snapshot_test_data/div-text/maxwidth-tight-text.json",

    // div-text.range_builder not supported
    "snapshot_test_data/div-text/range_builder.json",

    // div-text.range.background not supported
    "snapshot_test_data/div-text/ranges-background-text.json",
    "snapshot_test_data/div-text/text_range_with_cloud_background.json",
    "snapshot_test_data/div-text/text_with_cloud_background_alignment.json",
    "snapshot_test_data/div-text/text_with_cloud_background_padding.json",

    // div-text.range.top_offset not supported
    "snapshot_test_data/div-text/ranges-line-height-top-offset.json",

    // div-text.range.baseline_offset not supported
    "snapshot_test_data/div-text/text_range_baseline_offset.json",

    // div-text.range.alignment_vertical not supported
    "snapshot_test_data/div-text/text_range_vertical_alignment.json",

    // invalid scaling
    "snapshot_test_data/image-formats/svg/svg_background.json",
    "snapshot_test_data/image-formats/svg/svg_background_blur.json",
    "snapshot_test_data/image-formats/svg/svg_image.json",
    "snapshot_test_data/image-formats/svg/svg_image_blur.json",
    "snapshot_test_data/image-formats/svg/svg_image_tint.json",
    "snapshot_test_data/image-formats/svg/svg_in_gif_image.json",
    "snapshot_test_data/image-formats/svg/svg_local.json",
    "snapshot_test_data/image-formats/svg/svg_preview.json",
    "snapshot_test_data/image-formats/svg/svg_preview_blur.json",
    "snapshot_test_data/image-formats/svg/svg_preview_in_gif_image.json",
    "snapshot_test_data/image-formats/svg/svg_preview_url_in_gif_image.json",
    "snapshot_test_data/image-formats/svg/svg_wrap_content.json",

    // div-text.images not supported
    "snapshot_test_data/image-formats/svg/svg_text_image.json",
    "snapshot_test_data/image-formats/svg/svg_text_image_tint.json",
)
