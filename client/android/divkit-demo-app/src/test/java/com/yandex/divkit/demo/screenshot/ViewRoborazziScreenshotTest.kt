package com.yandex.divkit.demo.screenshot

import android.app.Application
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.createBitmap
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.LosslessWebPImageIoFormat
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.provideRoborazziContext
import com.yandex.div.rule.SetDataRule
import com.yandex.div.rule.UiReadyChecker
import com.yandex.div.test.SNAPSHOT_TEST_CASES_PATH
import com.yandex.div.test.ScreenshotTests
import com.yandex.div.test.chain
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils
import com.yandex.div.test.crossplatform.TEST_DATA_PATH
import com.yandex.div.test.viewDeviceScreenshotCases
import com.yandex.divkit.demo.Container
import org.json.JSONObject
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.ExternalResource
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.util.concurrent.TimeUnit

/** Static View-renderer counterparts of the Compose Roborazzi JSON cases. */
@Config(
    application = Application::class,
    sdk = [35],
    qualifiers = "w360dp-h728dp-xxhdpi",
)
@Category(ScreenshotTests::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(ParameterizedRobolectricTestRunner::class)
@OptIn(ExperimentalRoborazziApi::class)
class ViewRoborazziScreenshotTest(private val case: String, private val testCase: JSONObject) {

    private val activityRule = ActivityScenarioRule(DivScreenshotActivity::class.java)

    private val screenshotRule = RoborazziRule(
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

    private val initializationRule = object : ExternalResource() {
        override fun before() {
            assumeTrue(provideRoborazziContext().options.taskType.isEnabled())
            Container.initialize(getApplicationContext())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    @get:Rule
    val rules: TestRule = screenshotRule
        .chain(initializationRule)
        .chain(activityRule)
        .chain(SetDataRule(testCase, activityRule))

    @Before
    fun setUp() = awaitScreenshotReady()

    @Test
    fun initial() = captureScreenshot()

    @Test
    fun rebind() {
        activityRule.scenario.onActivity { it.setDivData(testCase) }
        awaitScreenshotReady()
        captureScreenshot()
    }

    private fun awaitScreenshotReady() {
        val checker = UiReadyChecker()
        val looper = shadowOf(Looper.getMainLooper())
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30)
        do {
            looper.idleFor(16, TimeUnit.MILLISECONDS)
            var ready = false
            activityRule.scenario.onActivity { ready = checker.isReady(it.divView) }
            if (ready) return
        } while (System.nanoTime() < deadline)
        error("Screenshot UI did not become ready")
    }

    private fun captureScreenshot() {
        lateinit var view: View
        activityRule.scenario.onActivity { view = it.divView }
        if (view.width > 0 && view.height > 0) {
            view.captureRoboImage()
            return
        }

        // Empty layouts are valid test cases, but cannot be captured as a zero-sized bitmap.
        val bitmap = createBitmap(width = 192, height = 48)
        Canvas(bitmap).drawText("<empty view>", 0f, 32f, Paint().apply { textSize = 32f })
        bitmap.captureRoboImage()
    }

    companion object {
        val snapshotTestCases = File(TEST_DATA_PATH, SNAPSHOT_TEST_CASES_PATH)
        private val cases = ParsingUtils.parseFiles(snapshotTestCases) { file, json ->
            val name = file.relativeTo(snapshotTestCases).invariantSeparatorsPath
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
