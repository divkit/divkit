package com.yandex.div.compose.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.dropbox.differ.SimpleImageComparator
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.LosslessWebPImageIoFormat
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.provideRoborazziContext
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div2.DivData
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

private const val EMPTY_VIEW_TAG = "empty_view"

@OptIn(ExperimentalCoroutinesApi::class)
class ComposeContentRoborazziRule(failOnError: Boolean = true) : TestRule {

    private val painterTracker = ImagePainterTracker()
    private val testDispatcher = UnconfinedTestDispatcher()
    private var isViewEmpty by mutableStateOf(false)

    val reporter = TestReporter().also { it.failOnError = failOnError }
    val testScope = TestScope(testDispatcher)

    val divContext = DivContext(
        getApplicationContext(),
        divConfiguration {
            extensionHandlers = mapOf("markdown" to SolidBackgroundExtensionHandler())
            fontSourceProvider = TestFontSourceProvider()
            imageLoaderConfiguration = LocalImageLoaderConfiguration(getApplicationContext())
            networkClient = LocalNetworkClient()
            reporter = this@ComposeContentRoborazziRule.reporter
        },
        DivDebugConfiguration(
            coroutineScope = testScope,
            imagePainterStateListener = painterTracker::onStateChanged,
            ioDispatcher = testDispatcher,
            timeSource = testScope.testScheduler.timeSource,
        )
    )

    val composeRule = createComposeRule().apply {
        registerIdlingResource(painterTracker)
    }

    lateinit var viewConfiguration: ViewConfiguration
        private set

    @OptIn(ExperimentalRoborazziApi::class)
    private val options = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(
            changeThreshold = 0.001f,
            imageComparator = SimpleImageComparator(maxDistance = 0.015f),
        ),
        recordOptions = RoborazziOptions.RecordOptions(imageIoFormat = LosslessWebPImageIoFormat())
    )

    private val ruleChain = RuleChain.outerRule(composeRule)
        .around(RoborazziRule(RoborazziRule.Options(roborazziOptions = options)))

    override fun apply(base: Statement, description: Description): Statement =
        ruleChain.apply(base, description)

    fun setContent(configuration: ScreenshotTestConfiguration): DivData {
        val data = configuration.parseDivData()
        composeRule.setContent {
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalLayoutDirection provides configuration.layoutDirection
            ) {
                viewConfiguration = LocalViewConfiguration.current
                Box(
                    Modifier.onSizeChanged { isViewEmpty = it.width == 0 || it.height == 0 }
                ) { DivView(data = data) }
                if (isViewEmpty) {
                    Text("<Empty>", modifier = Modifier.testTag(EMPTY_VIEW_TAG))
                }
            }
        }
        return data
    }

    fun waitForIdle() {
        composeRule.waitForIdle()
    }

    @OptIn(ExperimentalRoborazziApi::class)
    fun captureScreenshot(
        fileName: String,
        id: String? = null,
        roborazziOptions: RoborazziOptions = provideRoborazziContext().options,
    ) {
        val file = File("src/test/screenshots/$fileName.webp")
        file.parentFile?.mkdirs()

        val target = id?.let {
            composeRule
                .onNodeWithTag(id, useUnmergedTree = true)
                .assertExists("Failed to find screenshot target: $id")
        } ?: let {
            val emptyView = composeRule.onNodeWithTag(EMPTY_VIEW_TAG)
            if (emptyView.isDisplayed()) emptyView else composeRule.onRoot()
        }

        target.captureRoboImage(file.path, roborazziOptions)
    }
}

private class SolidBackgroundExtensionHandler : DivExtensionHandler {
    @Composable
    override fun Content(
        modifier: Modifier,
        environment: DivExtensionEnvironment,
        content: @Composable (Modifier) -> Unit
    ) = content(modifier.background(Color.Red))
}
