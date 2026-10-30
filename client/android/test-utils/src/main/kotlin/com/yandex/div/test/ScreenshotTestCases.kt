package com.yandex.div.test

const val SNAPSHOT_TEST_CASES_PATH = "snapshot_test_data"
const val INTERACTIVE_SCREENSHOT_CASES_PATH = "interactive_snapshot_test_data"
const val IMAGE_LOADER_SCREENSHOT_CASES_PATH = "ui_test_data/image-loaders"

/**
 * Static cases excluded from JVM View screenshot tests. Roborazzi is the default runner.
 *
 * Keep blur, shadows, video previews and cases with native rendering differences on the device runner.
 * Keep both separator-stroke cases on the device runner because wide strokes differ in bitmap rendering.
 * Interactive and integration screenshot suites keep their device coverage.
 * Check templates and nested items as well as the root element when adding an exception.
 */
val viewDeviceScreenshotCases: Set<String> = setOf(
    "div-background/blur.json",
    "div-border/background-under-round-border.json",
    "div-container/baseline-with-images.json",
    "div-container/clip-to-bounds-with-shadows.json",
    "div-container/overlap-children-shadows.json",
    "div-container/separator-stroke.json",
    "div-container/wrap/separator-stroke.json",
    "div-gif-image/shadow.json",
    "div-image/blur-with-big-radius.json",
    "div-image/blur.json",
    "div-image/shadow.json",
    "div-input/all-attributes.json",
    "div-input/min-height.json",
    "div-input/with-native-interface-no-background.json",
    "div-input/without-text.json",
    "div-pager/item-shadow-in-pager.json",
    "div-select/all-attributes.json",
    "div-shadow/block-with-alpha-and-shadow.json",
    "div-shadow/shadow-alpha-combinations.json",
    "div-shadow/shadow-with-alpha.json",
    "div-shadow/shadow-with-blur.json",
    "div-shadow/shadow-with-color-alpha-badge.json",
    "div-shadow/shadow-with-color.json",
    "div-shadow/shadow-with-offset.json",
    "div-text/all_attributes.json",
    "div-text/blur-background.json",
    "div-text/corner-radius-clamp.json",
    "div-text/custom_shadow.json",
    "div-text/shadow.json",
    "div-text/text-shadow-alpha.json",
    "div-text/text-shadow.json",
    "div-video/video-preview-scale-fill.json",
    "div-video/video-preview-scale-fit.json",
    "div-video/video-preview-scale-no-scale.json",
    "image-formats/animated-webp/animated_webp_background_blur.json",
    "image-formats/animated-webp/animated_webp_image_blur.json",
    "image-formats/animated-webp/animated_webp_preview_blur.json",
    "image-formats/gif/gif_background_blur.json",
    "image-formats/gif/gif_image_blur.json",
    "image-formats/gif/gif_preview_blur.json",
    "image-formats/png/png_background_blur.json",
    "image-formats/png/png_image_blur.json",
    "image-formats/png/png_preview_blur.json",
    "image-formats/svg/svg_background_blur.json",
    "image-formats/svg/svg_image_blur.json",
    "image-formats/svg/svg_preview_blur.json",
    "image-formats/webp/webp_background_blur.json",
    "image-formats/webp/webp_image_blur.json",
    "image-formats/webp/webp_preview_blur.json",
)

val viewDeviceIgnoredScreenshotCases = listOf(
    "interactive_snapshot_test_data/div-select/popup-geometry.json",
    "snapshot_test_data/div-text/all_attributes.json",
    "snapshot_test_data/div-container/baseline-with-images.json",
)

// Run only tests that are not supported by RoborazziScreenshotTest.
val composeDeviceScreenshotCases = setOf(
    // Image blur uses RenderScript, which is not supported in Robolectric.
    "snapshot_test_data/div-background/blur.json",
    "snapshot_test_data/div-image/blur-with-big-radius.json",
    "snapshot_test_data/div-image/blur.json",
    "snapshot_test_data/div-text/blur-background.json",
    "snapshot_test_data/image-formats/animated-webp/animated_webp_background_blur.json",
    "snapshot_test_data/image-formats/animated-webp/animated_webp_image_blur.json",
    "snapshot_test_data/image-formats/animated-webp/animated_webp_preview_blur.json",
    "snapshot_test_data/image-formats/gif/gif_background_blur.json",
    "snapshot_test_data/image-formats/gif/gif_image_blur.json",
    "snapshot_test_data/image-formats/gif/gif_preview_blur.json",
    "snapshot_test_data/image-formats/png/png_background_blur.json",
    "snapshot_test_data/image-formats/png/png_image_blur.json",
    "snapshot_test_data/image-formats/png/png_preview_blur.json",
    "snapshot_test_data/image-formats/webp/webp_background_blur.json",
    "snapshot_test_data/image-formats/webp/webp_image_blur.json",
    "snapshot_test_data/image-formats/webp/webp_preview_blur.json",
)

val composeRoborazziIgnoredScreenshotCases = setOf(

    // div-container.aspect not fully supported
    "div-container/aspect/wrap_content-width-horizontal.json",
    "div-container/aspect/wrap_content-width-overlap.json",
    "div-container/aspect/wrap_content-width-overlap-fixed-height.json",
    "div-container/aspect/wrap_content-width-overlap-match_parent-height.json",
    "div-container/aspect/wrap_content-width-vertical.json",
    "div-container/aspect/wrap_content-width-vertical-constrained-child-not-fit.json",
    "div-container/aspect/wrap_content-width-vertical-fixed-height.json",
    "div-container/aspect/wrap_content-width-vertical-match_parent-child-fits.json",
    "div-container/aspect/wrap_content-width-vertical-match_parent-child-not-fit.json",
    "div-container/aspect/wrap_content-width-vertical-match_parent-height.json",

    // size_provider extension not supported
    "div-container/size_provider.json",
    "div-container/size_provider_recursive.json",

    // match_parent min_size next to wrap_content constrained siblings not supported, DIVKIT-10714
    "div-container/match-parent-min-size-with-constrained-siblings.json",

    // div-pager vertical not supported
    "div-pager/vertical-pager-custom-neighbour-page-width.json",
    "div-pager/vertical-pager-custom-neighbour-page-width-with-paddings.json",
    "div-pager/vertical-pager-custom-page-width.json",
    "div-pager/vertical-pager-custom-page-width-with-paddings.json",
    "div-pager/vertical-pager-neighbour-page-size-mode-alignment-end.json",
    "div-pager/vertical-pager-neighbour-page-size-mode-alignment-start.json",
    "div-pager/vertical-pager-neighbour-page-size-mode-item-spacing.json",
    "div-pager/vertical-pager-neighbour-page-size-mode-two-pages.json",
    "div-pager/vertical-pager-neighbour-page-width-0-with-item-spacing.json",
    "div-pager/vertical-pager-neighbour-page-width-0-with-paddings.json",
    "div-pager/vertical-pager-page-size-mode-alignment-end.json",
    "div-pager/vertical-pager-page-size-mode-alignment-start.json",
    "div-pager/vertical-pager-page-size-mode-item-spacing.json",
    "div-pager/vertical-pager-page-width-100-with-item-spacing.json",
    "div-pager/vertical-pager-page-width-100-with-paddings.json",
    "div-pager/vertical-pager-wrap-content-height.json",
    "div-pager/vertical-pager-wrap-content-height-with-paddings.json",
    "div-pager/vertical-pager-wrap-content-size-mode-alignment-end.json",
    "div-pager/vertical-pager-wrap-content-size-mode-alignment-start.json",
    "div-pager/vertical-pager-wrap-content-size-mode-with-item-spacing.json",

    // div-gallery.item_builder not supported
    "div-gallery/item-builder/item-builder.json",
    "div-gallery/item-builder/item-builder-with-local-variables.json",
    "div-gallery/item-builder/nested-builders.json",
    "div-gallery/item-builder/non-unique-matched-selectors.json",


    // div-pager.item_builder not supported
    "div-pager/item-builder/item-builder.json",
    "div-pager/item-builder/item-builder-with-local-variables.json",
    "div-pager/item-builder/nested-builders.json",
    "div-pager/item-builder/non-unique-matched-selectors.json",

    // div-text ellipsis images not supported
    "div-text/ellipsis-with-image.json",
    "div-text/ellipsis_builder.json",

    // div-text.range.line_height not supported
    "div-text/line-height/multi-line-text-with-image.json",

    // div-text.image_builder not supported
    "div-text/image_builder.json",

    // markdown extension not supported
    "div-text/markdown-extension.json",

    // div-text.tighten_width not supported
    "div-text/maxwidth-tight-text.json",

    // div-text.range_builder not supported
    "div-text/range_builder.json",

    // div-text.range.top_offset not supported
    "div-text/ranges-line-height-top-offset.json",

    // div-text.ranges.top_offset not supported
    "div-text/ranges-intersection-top-offset.json",

    // div-text.range.baseline_offset not supported
    "div-text/text_range_baseline_offset.json",

    // div-text.range.alignment_vertical not supported
    "div-text/text_range_vertical_alignment.json",

    // test div-customs required
    "div-size/match-parent/overlap/height/platform-wrap-content-container-height.json",
    "div-size/match-parent/overlap/custom-with-fixed-size.json",

    // image blur uses render script which is not supported in Robolectric
    "div-background/blur.json",
    "div-image/blur.json",
    "div-image/blur-with-big-radius.json",
    "div-text/blur-background.json",
    "image-formats/animated-webp/animated_webp_background_blur.json",
    "image-formats/animated-webp/animated_webp_image_blur.json",
    "image-formats/animated-webp/animated_webp_preview_blur.json",
    "image-formats/gif/gif_background_blur.json",
    "image-formats/gif/gif_image_blur.json",
    "image-formats/gif/gif_preview_blur.json",
    "image-formats/png/png_background_blur.json",
    "image-formats/png/png_image_blur.json",
    "image-formats/png/png_preview_blur.json",
    "image-formats/svg/svg_background_blur.json",
    "image-formats/svg/svg_image_blur.json",
    "image-formats/svg/svg_preview_blur.json",
    "image-formats/webp/webp_background_blur.json",
    "image-formats/webp/webp_image_blur.json",
    "image-formats/webp/webp_preview_blur.json",
)

val composeRoborazziInteractiveScreenshotCases = setOf(
    "div-action/base.json",
    "div-action/focus-actions.json",
    "div-action/is-enabled.json",
    "div-action/set-state.json",
    "div-action/set-variable.json",
    "div-container/base-properties.json",
    "div-container/item_builder.json",
    "div-container/visibility.json",
    "div-extension/is-enabled.json",
    "div-gallery/paging-paddings.json",
    "div-gallery/scroll-content-alignment-rtl.json",
    "div-gallery/scroll-content-alignment.json",
    "div-gallery/select-elements.json",
    "div-gallery/specific-properties.json",
    "div-gallery/state-preservation-basic.json",
    "div-gallery/state-preservation-multiple-items.json",
    "div-gallery/state-preservation-nested.json",
    "div-gallery/typed-actions.json",
    "div-gallery/vertical-paging-paddings.json",
    "div-grid/grid_layout.json",
    "div-indicator/shapes.json",
    "div-input/base-properties.json",
    "div-input/currency_input_mask.json",
    "div-input/expression_input_validator.json",
    "div-input/fixed_length_input_mask.json",
    "div-input/focus/blurred-and-focused-background-and-border.json",
    "div-input/focus/blurred-and-focused-background.json",
    "div-input/focus/blurred-and-focused-border.json",
    "div-input/focus/blurred-background-and-border.json",
    "div-input/focus/blurred-background.json",
    "div-input/focus/blurred-border.json",
    "div-input/focus/focused-background-and-border.json",
    "div-input/focus/focused-background.json",
    "div-input/focus/focused-border.json",
    "div-input/focus/selection.json",
    "div-input/highlight-color.json",
    "div-input/phone_input_mask.json",
    "div-input/regex_input_validator.json",
    "div-input/size-properties.json",
    "div-input/specific-properties.json",
    "div-input/text-properties.json",
    "div-input/visibility.json",
    "div-pager/horizontal-content-alignment.json",
    "div-pager/infinite-scroll.json",
    "div-pager/pager-selected-actions.json",
    "div-pager/select-elements.json",
    "div-pager/specific-properties.json",
    "div-pager/variables.json",
    "div-pager/vertical-content-alignment.json",
    "div-select/base-properties.json",
    "div-select/popup-geometry.json",
    "div-select/switching_options.json",
    "div-select/text-properties.json",
    "div-select/visibility.json",
    "div-slider/base-properties.json",
    "div-slider/ranges.json",
    "div-slider/shapes.json",
    "div-slider/thumb-style/thumb-primary-style-properties.json",
    "div-slider/thumb-style/thumb-secondary-style-properties.json",
    "div-slider/thumb-values/thumb-values-with-tick-marks.json",
    "div-slider/thumb-values/thumb-values-without-tick-marks.json",
    "div-slider/tick-mark-style/tick-mark-active-style-properties.json",
    "div-slider/tick-mark-style/tick-mark-inactive-style-properties.json",
    "div-slider/track-style/track-active-style-properties.json",
    "div-slider/track-style/track-inactive-style-properties.json",
    "div-slider/visibility.json",
    "div-state/base-properties.json",
    "div-switch/base-properties.json",
    "div-switch/switch-properties.json",
    "div-text/text-properties.json",
    "div-text/text_focused_text_color.json",
    "div-text/text_range_without_bound.json",
    "div-text/truncate-policy.json",
    "local-variables/item-builder-data-change-another-id.json",
    "local-variables/item-builder-data-change-no-id-same-data-and-index.json",
    "local-variables/item-builder-data-change-no-id-same-data-another-index.json",
    "local-variables/item-builder-data-change-no-id-same-index-another-data.json",
    "local-variables/item-builder-data-change-same-id-data-and-index.json",
)
