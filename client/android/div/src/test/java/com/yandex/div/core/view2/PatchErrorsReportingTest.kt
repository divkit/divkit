package com.yandex.div.core.view2

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivErrorsReporter
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Tests for patch error reporting in [Div2View].
 */
@RunWith(RobolectricTestRunner::class)
class PatchErrorsReportingTest {
    private val divImageLoader = mock<DivImageLoader>()
    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val reporter = mock<DivErrorsReporter>()
    private val div2Context = Div2Context(
        activity,
        DivConfiguration.Builder(divImageLoader)
            .divErrorsReporter(reporter)
            .build()
    )

    private val div2View = Div2View(div2Context)
    private val tag = DivDataTag("patch_errors_test")
    private val cardJson = JSONObject(CARD)
    private val patchJson = JSONObject(PATCH)
    private val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)

    @Test
    fun `patch application produces errors inside divView`() {
        // ARRANGE
        val divData = DivData(environment, cardJson)
        div2View.setData(divData, tag)
        verify(reporter, never()).onRuntimeError(any(), any(), any())

        // ACT
        val divPatch = DivPatch(environment, patchJson)
        val oldData = div2View.divData
        val applied = div2View.applyPatch(divPatch)

        // ASSERT
        Assert.assertFalse(applied)
        Assert.assertSame(oldData, div2View.divData)
        verify(reporter).onRuntimeError(any(), any(), any())
    }
}

private const val CARD = """
{
    "log_id": "test_card",
    "states": [
        {
            "state_id": 0,
            "div": {
                "id": "patch_target",
                "type": "container",
                "items": [
                    {
                        "type": "text",
                        "text": "Hello World"
                    }
                ]
            }
        }
    ]
}
"""

private const val PATCH = """
{
    "mode": "partial",
    "changes": [
        {
            "id": "patch_target",
            "items": [
                {
                    "type": "invalid_type",
                    "text": "This should cause an error"    
                }
            ]
        }
    ]
}
"""
