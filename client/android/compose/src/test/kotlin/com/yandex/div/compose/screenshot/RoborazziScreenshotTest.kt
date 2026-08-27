package com.yandex.div.compose.screenshot

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.LosslessWebPImageIoFormat
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.internal.DivDebugConfiguration
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import kotlin.test.Test

/**
 * JVM-based snapshot test for the DivKit Compose renderer using Roborazzi.
 *
 * Verify against goldens:
 * ```
 * ./gradlew :compose:verifyRoborazziDebug --tests "*.RoborazziScreenshotTest"
 * ```
 *
 * Record golden screenshots:
 * ```
 * ./gradlew :compose:verifyAndRecordRoborazziDebug --tests "*.RoborazziScreenshotTest"
 * ```
 *
 * Verify/record a single file (path relative to snapshot_test_data):
 * ```
 * ./gradlew :compose:verifyRoborazziDebug --tests "*.RoborazziScreenshotTest" -PdivkitTestFilter=div-text/all_attributes.json
 * ./gradlew :compose:recordRoborazziDebug --tests "*.RoborazziScreenshotTest" -PdivkitTestFilter=div-text/all_attributes.json
 * ```
 *
 * Goldens are stored in `src/test/screenshots/` and committed to the repository.
 */
@Config(qualifiers = "w360dp-h728dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(ParameterizedRobolectricTestRunner::class)
class RoborazziScreenshotTest(
    name: String,
    private val file: File
) {
    private val goldenFile = File("src/test/screenshots/$name.webp").apply {
        parentFile?.mkdirs()
    }

    private val painterTracker = ImagePainterTracker()

    private val divContext = DivContext(
        baseContext = getApplicationContext(),
        configuration = DivConfiguration(
            fontSourceProvider = YandexSansFontSourceProvider(),
            imageLoaderConfiguration = LocalImageLoaderConfiguration(),
            reporter = TestReporter()
        ),
        debugConfiguration = DivDebugConfiguration(
            imagePainterStateListener = painterTracker::onStateChanged
        )
    )

    private val composeRule = createComposeRule().apply {
        registerIdlingResource(painterTracker)
    }

    @OptIn(ExperimentalRoborazziApi::class)
    @get:Rule
    val rule: RuleChain = RuleChain
        .outerRule(composeRule)
        .around(
            RoborazziRule(
                options = RoborazziRule.Options(
                    roborazziOptions = RoborazziOptions(
                        compareOptions = RoborazziOptions.CompareOptions(
                            changeThreshold = 0.005f
                        ),
                        recordOptions = RoborazziOptions.RecordOptions(
                            imageIoFormat = LosslessWebPImageIoFormat()
                        )
                    )
                )
            )
        )

    @Test
    fun test() {
        val configuration = ScreenshotTestConfiguration.parse(file)

        var isViewEmpty by mutableStateOf(false)

        composeRule.apply {
            setContent {
                CompositionLocalProvider(
                    LocalContext provides divContext,
                    LocalLayoutDirection provides configuration.layoutDirection
                ) {
                    if (isViewEmpty) {
                        Text("<Empty>")
                    } else {
                        DivView(
                            modifier = Modifier.testTag("DivView"),
                            data = configuration.data
                        )
                    }
                }
            }

            waitForIdle()

            val viewSize = onNodeWithTag("DivView").fetchSemanticsNode().size
            isViewEmpty = viewSize.width == 0 || viewSize.height == 0

            waitForIdle()

            onRoot().captureRoboImage(filePath = goldenFile.path)
        }
    }

    companion object {

        // Store parsed test cases to prevent multiple parsing by
        // ParameterizedRobolectricTestRunner
        private val cases: List<Array<Any>> = run {
            val fileName = System.getProperty("divkit.test.filter")
            if (fileName != null) {
                val file = File("$BASE_DIR/$fileName")
                listOf(arrayOf<Any>(file.testName, file))
            }

            getJsonFiles(BASE_DIR)
                .filter { it.relativeTo(BASE_DIR).path !in ignoredFiles }
                .map { file -> arrayOf(file.testName, file) }
        }

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        @Suppress("unused")
        fun cases() = cases
    }
}

private val BASE_DIR = File("../../../test_data/snapshot_test_data")

private val File.testName: String
    get() = relativeTo(BASE_DIR).path.removeSuffix(".json")

private fun getJsonFiles(dir: File): List<File> {
    val items = dir.listFiles() ?: return emptyList()
    val (directories, files) = items.partition { it.isDirectory }
    return buildList {
        addAll(files.sortedBy { it.name })
        addAll(directories.sortedBy { it.name }.flatMap { getJsonFiles(it) })
    }
}

private val ignoredFiles = setOf(
    // div-nine-patch-background not supported
    "div-background/nine-patch-rhombs.json",
    "div-background/nine-patch-rhombs-horizontal-insets.json",
    "div-background/nine-patch-rhombs-large-all-insets.json",
    "div-background/nine-patch-rhombs-large-bottom-inset.json",
    "div-background/nine-patch-rhombs-large-bottom-left-inset.json",
    "div-background/nine-patch-rhombs-large-bottom-left-right-inset.json",
    "div-background/nine-patch-rhombs-vertical-insets.json",
    "div-background/nine-patch-shape.json",

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

    // div-container.item_builder not supported
    "div-container/item_builder/index.json",
    "div-container/item_builder/item-builder.json",
    "div-container/item_builder/item-builder-priority.json",
    "div-container/item_builder/item-builder-with-local-variables.json",
    "div-container/item_builder/item-builder-with-nested-local-variables.json",
    "div-container/item_builder/nested-builders.json",
    "div-container/item_builder/non-unique-matched-selectors.json",

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

    // div-image.aspect not supported
    "div-image/aspect-wrap_content.json",
    "div-image/wrap-content-aspect-zero-constraints.json",
    "div-image/wrap-content-image-with-aspect-in-horizontal-match-parent-container.json",
    "div-image/wrap-content-image-with-aspect-in-horizontal-wrap-content-container.json",
    "div-image/wrap-content-image-with-aspect-in-overlap-match-parent-container.json",
    "div-image/wrap-content-image-with-aspect-in-overlap-wrap-content-container.json",
    "div-image/wrap-content-image-with-aspect-in-vertical-match-parent-container.json",
    "div-image/wrap-content-image-with-aspect-in-vertical-wrap-content-container.json",

    // div-filter.rtl_mirror not supported
    "div-image/rtl-filter.json",

    // label_image_preview extension not supported
    "div-image/custom-preview.json",

    // div-gallery.scroll_content_alignment not supported
    "div-gallery/default-item/horizontal-gallery-alignment-center.json",
    "div-gallery/default-item/horizontal-gallery-alignment-center-rtl.json",
    "div-gallery/default-item/horizontal-gallery-alignment-end.json",
    "div-gallery/default-item/horizontal-gallery-alignment-end-rtl.json",
    "div-gallery/default-item/horizontal-gallery-alignment-start.json",
    "div-gallery/default-item/horizontal-gallery-alignment-start-rtl.json",
    "div-gallery/default-item/horizontal-grid-gallery-alignment-center.json",
    "div-gallery/default-item/horizontal-grid-gallery-alignment-center-rtl.json",
    "div-gallery/default-item/horizontal-grid-gallery-alignment-end.json",
    "div-gallery/default-item/horizontal-grid-gallery-alignment-end-rtl.json",
    "div-gallery/default-item/horizontal-grid-gallery-alignment-start.json",
    "div-gallery/default-item/horizontal-grid-gallery-alignment-start-rtl.json",
    "div-gallery/default-item/vertical-gallery-alignment-center.json",
    "div-gallery/default-item/vertical-gallery-alignment-end.json",
    "div-gallery/default-item/vertical-gallery-alignment-start.json",
    "div-gallery/default-item/vertical-gallery-alignment-start-rtl.json",
    "div-gallery/default-item/vertical-grid-gallery-alignment-center.json",
    "div-gallery/default-item/vertical-grid-gallery-alignment-end.json",
    "div-gallery/default-item/vertical-grid-gallery-alignment-start.json",
    "div-gallery/default-item/vertical-grid-gallery-alignment-start-rtl.json",

    // div-gallery.item_builder not supported
    "div-gallery/item-builder/item-builder.json",
    "div-gallery/item-builder/item-builder-with-local-variables.json",
    "div-gallery/item-builder/nested-builders.json",
    "div-gallery/item-builder/non-unique-matched-selectors.json",

    // div-base.layout_provider not supported
    "div-layout-provider/layout-provider.json",

    // div-pager.item_builder not supported
    "div-pager/item-builder/item-builder.json",
    "div-pager/item-builder/item-builder-with-local-variables.json",
    "div-pager/item-builder/nested-builders.json",
    "div-pager/item-builder/non-unique-matched-selectors.json",

    // div-text.images not supported
    "div-text/line-height/multi-line-text-with-image.json",
    "div-text/line-height/single-line-text-with-image.json",
    "div-text/all_attributes.json",
    "div-text/custom-image-tint-color.json",
    "div-text/custom-text-alignment-with-attachments.json",
    "div-text/ellipsis-with-image.json",
    "div-text/ellipsis_builder.json",
    "div-text/image-reverse-indexing.json",
    "div-text/image-tint-mode.json",
    "div-text/image_builder.json",
    "div-text/images.json",
    "div-text/images_hyphenation.json",
    "div-text/text_image_vertical_alignment.json",
    "image-formats/animated-webp/animated_webp_text_image.json",
    "image-formats/animated-webp/animated_webp_text_image_tint.json",
    "image-formats/gif/gif_text_image.json",
    "image-formats/gif/gif_text_image_tint.json",
    "image-formats/png/png_text_image.json",
    "image-formats/png/png_text_image_tint.json",
    "image-formats/svg/svg_text_image.json",
    "image-formats/svg/svg_text_image_tint.json",
    "image-formats/webp/webp_text_image.json",
    "image-formats/webp/webp_text_image_tint.json",

    // div-text.range.mask not supported
    "div-text/mask.json",

    // markdown extension not supported
    "div-text/markdown-extension.json",

    // div-text.tighten_width not supported
    "div-text/maxwidth-tight-text.json",

    // div-text.range_builder not supported
    "div-text/range_builder.json",

    // div-text.range.background not supported
    "div-text/ranges-background-text.json",
    "div-text/text_range_with_cloud_background.json",
    "div-text/text_with_cloud_background_alignment.json",
    "div-text/text_with_cloud_background_padding.json",

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

    // intentional error/broken-state cases
    "div-slider/slider_with_error_and_warning.json",
    "div-tabs/selected-tab-broken-content.json",
)
