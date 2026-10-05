package com.yandex.divkit.demo.screenshot

/**
 * Static cases excluded from JVM View screenshot tests. Roborazzi is the default runner.
 *
 * Keep blur, shadows, video previews and cases with native rendering differences on the device runner.
 * Keep both separator-stroke cases on the device runner because wide strokes differ in bitmap rendering.
 * Interactive and integration screenshot suites keep their device coverage.
 * Check templates and nested items as well as the root element when adding an exception.
 */
internal val viewDeviceScreenshotCases: Set<String> = setOf(
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
