package com.yandex.div.legacy

import android.app.Activity
import com.yandex.alicekit.core.experiments.ExperimentConfig
import com.yandex.alicekit.core.widget.TypefaceProvider
import com.yandex.div.core.images.DivImageLoader
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivContextTest {

    val activity = Robolectric.setupActivity(Activity::class.java)
    val divConfiguration = mock<DivLegacyConfiguration>()
    val autoLogger = mock<DivAutoLogger>()
    val divLogger = mock<DivLogger>()
    val imageLoader = mock<DivImageLoader>()
    val typefaceProvider = mock<TypefaceProvider>()
    val actionHandler = mock<LegacyDivActionHandler>()
    val experimentConfig = mock<ExperimentConfig>()

    @Before
    fun setUp() {
        whenever(divConfiguration.autoLogger).thenReturn(autoLogger)
        whenever(divConfiguration.divLogger).thenReturn(divLogger)
        whenever(divConfiguration.imageLoader).thenReturn(imageLoader)
        whenever(divConfiguration.actionHandler).thenReturn(actionHandler)
        whenever(divConfiguration.experimentConfig).thenReturn(experimentConfig)

        // Enable all flags
        whenever(experimentConfig.getBooleanValue(any())).thenReturn(true)
    }

    @Test(expected = AssertionError::class)
    fun `context creation in background failed`() {
        runAsync {
            DivContext(activity, divConfiguration)
        }
    }

    @Test
    fun `context warm up in background successful`() {
        val context = DivContext(activity, divConfiguration)
        runAsync {
            context.warmUp()
        }
    }
}
