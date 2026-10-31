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

val composeIgnoredInteractiveScreenshotCases = setOf(
    "div-action/custom-action.json",
    "div-action/focus-revealed-element.json",
    "div-action/scope-id/focus-element.json",
    "div-action/scope-id/set-cursor-position.json",
    "div-action/simple-tap.json",
    "div-action/typed-set-state.json",
    "div-container/item-builder-actions.json",
    "div-container/patch_container.json",
    "div-container/patch_inside_container.json",
    "div-container/separators.json",
    "div-container/specific-properties.json",
    "div-container/visibility_actions_visible_block.json",
    "div-container/wrap/separators.json",
    "div-extension/gesture.json",
    "div-gallery/base-properties.json",
    "div-gallery/expand-collapse-default.json",
    "div-gallery/item-builder.json",
    "div-gallery/items_visibility.json",
    "div-gallery/patch_gallery.json",
    "div-gallery/state-change-action-synchronization.json",
    "div-gallery/state-change-variable-synchronization.json",
    "div-gallery/state-preservation-with-variables.json",
    "div-gallery/visibility.json",
    "div-gallery/wrap-content-height-after-scroll.json",
    "div-grid/patch_grid.json",
    "div-grid/patch_inside_grid.json",
    "div-image/blur.json",
    "div-image/variables_div_image.json",
    "div-input/capture-focus-on-action.json",
    "div-layout-provider/layout-provider-recursive.json",
    "div-pager/base-properties.json",
    "div-pager/item_count.json",
    "div-pager/item_count_builder.json",
    "div-pager/multi-page-scroll.json",
    "div-pager/patch_pager.json",
    "div-pager/visibility.json",
    "div-state/clip-to-bounds-with-shadow.json",
    "div-state/multiple-root-state-switching.json",
    "div-state/patch_state_partial.json",
    "div-state/patch_state_transactional_broken.json",
    "div-state/patch_state_transactional_success.json",
    "div-tabs/patch_tabs_partial.json",
    "div-tabs/patch_tabs_transactional_broken.json",
    "div-tabs/patch_tabs_transactional_success.json",
    "div-text/background-image-blur.json",
    "div-timer/timer-end-actions-tick-actions.json",
    "div-transform/rotation-after-rebind.json",
    "local-variables/global-and-local.json",
)
