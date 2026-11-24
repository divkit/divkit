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
    // Flaky on API 26.
    "interactive_snapshot_test_data/div-action/focus-revealed-element.json",
    // Flaky on API 26.
    "interactive_snapshot_test_data/div-container/wrap/separators.json",
    // Flaky on API 26.
    "interactive_snapshot_test_data/div-gallery/scroll-content-alignment-rtl.json",
    // Flaky on API 26.
    "interactive_snapshot_test_data/div-input/focus/disabled-input.json",
    // Flaky on API 26.
    "interactive_snapshot_test_data/div-layout-provider/layout-provider-recursive.json",
    // Flaky on API 26, 32.
    "interactive_snapshot_test_data/div-pager/multi-page-scroll.json",
    // Flaky on API 26.
    "interactive_snapshot_test_data/div-state/base-properties.json",

    "interactive_snapshot_test_data/div-select/popup-geometry.json",
    "snapshot_test_data/div-text/all_attributes.json",
    "snapshot_test_data/div-container/baseline-with-images.json",

    // View stops ring scroll_backward/scroll_forward in a gallery at a wrong offset; Compose, iOS and Web match.
    "interactive_snapshot_test_data/div-gallery/scroll-actions.json",

    // A vertical gallery inside a vertical gallery is not swipeable without restrict_parent_scroll.
    "interactive_snapshot_test_data/scroll-to/nested-gallery-both-axes.json",

    // The View runner cannot find targets or take snapshots in a tooltip window yet, DIVKIT-10793
    "interactive_snapshot_test_data/div-tooltip/tooltip-positions.json",
    "interactive_snapshot_test_data/div-tooltip/tooltip-with-bring-to-top-view.json",
    "interactive_snapshot_test_data/div-tooltip/tooltips-close-button.json",
    "interactive_snapshot_test_data/div-tooltip/tooltips-close-by-duration.json",
    "interactive_snapshot_test_data/div-tooltip/tooltips-close-by-visibility-action.json",
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
    // size_provider extension not supported
    "div-container/size_provider.json",
    "div-container/size_provider_recursive.json",

    // markdown extension not supported
    "div-text/markdown-extension.json",

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
    // Image blur uses RenderScript, which is not supported in Robolectric.
    "div-image/blur.json",
    "div-text/background-image-blur.json",

    // Long tap on a child without longtap_actions does not reach the parent, DIVKIT-10433
    "div-action/button-actions.json",

    // Visibility actions ignore visibility: invisible, so showing the text again logs no event.
    "div-text/visibility.json",

    "div-action/custom-action.json",
    "div-action/scope-id/focus-element.json",
    "div-action/scope-id/set-cursor-position.json",
    "div-extension/gesture.json",
    "div-gallery/state-change-action-synchronization.json",
    "div-gallery/state-change-variable-synchronization.json",
    "div-gallery/state-preservation-with-variables.json",
    "div-gallery/wrap-content-height-after-scroll.json",
    "div-input/capture-focus-on-action.json",
    "div-state/multiple-root-state-switching.json",

    // A paging gallery flung into its end padding does not snap back to the last item.
    "div-gallery/items-position.json",

    // Visibility actions of a visible element do not trigger when is_enabled becomes true.
    "scroll-to/visibility-enabled-after-scroll.json",

    // set_state with a path through an inactive state also switches that state.
    "div-gallery/gallery-bottom-multiline.json",
    "div-gallery/gallery-bottom-multiline-selected-action.json",
    "div-gallery/gallery-top-multiline.json",
)
