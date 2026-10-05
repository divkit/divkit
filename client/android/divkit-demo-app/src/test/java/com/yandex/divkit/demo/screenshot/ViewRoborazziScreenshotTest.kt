package com.yandex.divkit.demo.screenshot

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.graphics.createBitmap
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.LosslessWebPImageIoFormat
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.provideRoborazziContext
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils
import com.yandex.divkit.demo.Container
import org.hamcrest.Matcher
import org.json.JSONObject
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.util.concurrent.TimeUnit

/** Static View-renderer counterparts of the Compose Roborazzi JSON cases. */
@Config(
    application = ViewScreenshotTestApplication::class,
    sdk = [35],
    qualifiers = "w360dp-h728dp-xxhdpi",
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(ParameterizedRobolectricTestRunner::class)
@OptIn(ExperimentalRoborazziApi::class)
class ViewRoborazziScreenshotTest(private val case: String, private val testCase: JSONObject) {

    @get:Rule
    val screenshotRule = RoborazziRule(
        options = RoborazziRule.Options(
            outputFileProvider = { _, directory, _ ->
                File(directory, "${case.removeSuffix(".json")}.webp")
            },
            roborazziOptions = RoborazziOptions(
                compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.005f),
                recordOptions = RoborazziOptions.RecordOptions(imageIoFormat = LosslessWebPImageIoFormat()),
            )
        )
    )

    @Test
    fun initial() {
        // Arrange: plain unit-test runs leave capture to the dedicated Roborazzi job.
        assumeTrue(provideRoborazziContext().options.taskType.isEnabled())
        // Act: use the same Activity, fonts, extensions and local images as instrumented View tests.
        ActivityScenario.launch<DivScreenshotActivity>(createIntent()).use { scenario ->
            scenario.onActivity { activity -> activity.setDivData(testCase) }
            awaitScreenshotReady()

            // Assert: Roborazzi compares the rendered card with its own JVM reference.
            scenario.onActivity { activity -> captureScreenshot(activity.divView) }
        }
    }

    @Test
    fun rebind() {
        // Arrange: bind the card once, as in the instrumented rebind suite.
        assumeTrue(provideRoborazziContext().options.taskType.isEnabled())
        ActivityScenario.launch<DivScreenshotActivity>(createIntent()).use { scenario ->
            scenario.onActivity { activity -> activity.setDivData(testCase) }
            awaitScreenshotReady()

            // Act: bind the same JSON to the existing Div2View.
            scenario.onActivity { activity ->
                activity.divView.tag = null
                activity.setDivData(testCase)
            }
            awaitScreenshotReady()

            // Assert: the rebound card has the same visual reference.
            scenario.onActivity { activity -> captureScreenshot(activity.divView) }
        }
    }

    private fun createIntent(): Intent {
        return Intent(getApplicationContext(), DivScreenshotActivity::class.java)
            .putExtra(DivScreenshotActivity.EXTRA_DIV_IMAGE_LOADER_NAME, DivScreenshotActivity.IMAGE_LOADER_LOCAL)
    }

    private fun captureScreenshot(view: View) {
        if (view.width == 0 || view.height == 0) {
            // Empty layouts are valid test cases, but cannot be captured as a zero-sized bitmap.
            val bitmap = createBitmap(width = 192, height = 48)
            Canvas(bitmap).drawText("<empty view>", 0f, 32f, Paint().apply { textSize = 32f })
            bitmap.captureRoboImage()
        } else {
            view.captureRoboImage()
        }
    }

    companion object {
        private val cases = ParsingUtils.parseFiles("snapshot_test_data") { file, json ->
            val name = file.relativeTo(File("../../../test_data/snapshot_test_data")).invariantSeparatorsPath
            if (name in viewDeviceScreenshotCases) {
                return@parseFiles emptyList()
            }
            listOf(ParsingResult.Success(arrayOf(name, json)))
        }.map { it.getOrThrow() }.also { cases ->
            require(cases.isNotEmpty()) { "No View Roborazzi cases selected" }
        }

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases() = cases
    }
}

private fun awaitScreenshotReady() {
    onView(isRoot()).perform(object : ViewAction {
        override fun getDescription() = "wait for the bound card, local images, inline previews and layout"

        override fun getConstraints(): Matcher<View> = isRoot()

        override fun perform(uiController: UiController, view: View) {
            val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30)
            do {
                // Advance Android's event loop so posted layout and image callbacks can finish.
                uiController.loopMainThreadForAtLeast(16)
                val card = view.findViewWithTag<View>(DivScreenshotActivity.SCREENSHOT_VIEW_TAG)
                if (card != null && card.isLaidOut && !card.isLayoutRequested &&
                    Container.imageLoader.isIdle && !card.hasPendingImageDecoding()
                ) {
                    return
                }
            } while (System.nanoTime() < deadline)
            error("Screenshot card did not finish binding, image loading, inline preview decoding and layout")
        }
    })
}
