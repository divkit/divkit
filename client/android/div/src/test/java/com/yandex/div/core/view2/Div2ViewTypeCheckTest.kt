package com.yandex.div.core.view2

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingException
import com.yandex.div.json.ParsingExceptionReason
import com.yandex.div2.DivData
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Div2ViewTypeCheckTest {
    private val divImageLoader = mock<DivImageLoader>()
    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val div2Context = Div2Context(
        baseContext = activity,
        configuration = DivConfiguration.Builder(divImageLoader)
            .build()
    )
    private val tag = DivDataTag("tag")
    private val parsingErrors = mutableListOf<Throwable>()
    private val parsingEnvironment = DivParsingEnvironment(
        logger = { e -> parsingErrors.add(e) }
    )

    private val div2View = Div2View(div2Context)

    @Test
    fun `type mismatch at non-optional fields`() {
        val divData = DivData(parsingEnvironment, JSONObject(TYPE_MISMATCH_AT_FIELD))
        div2View.setData(divData, tag)
        Assert.assertEquals(1, parsingErrors.size)
        val cause = parsingErrors.first().cause as ParsingException
        Assert.assertEquals(ParsingExceptionReason.TYPE_MISMATCH, cause.reason)
    }

    @Test
    fun `type mismatch at optional fields`() {
        val divData = DivData(parsingEnvironment, JSONObject(TYPE_MISMATCH_AT_OPTIONAL_FIELD))
        div2View.setData(divData, tag)
        Assert.assertEquals(1, parsingErrors.size)
        val cause = parsingErrors.first() as ParsingException
        Assert.assertEquals(ParsingExceptionReason.TYPE_MISMATCH, cause.reason)
    }

    @Test
    fun `type mismatch at list`() {
        val divData = DivData(parsingEnvironment, JSONObject(TYPE_MISMATCH_AT_LIST))
        div2View.setData(divData, tag)
        Assert.assertEquals(3, parsingErrors.size)
        val parsingExceptions = parsingErrors.map { it as ParsingException }
        Assert.assertEquals(ParsingExceptionReason.TYPE_MISMATCH, parsingExceptions[0].reason)
        Assert.assertEquals(ParsingExceptionReason.TYPE_MISMATCH, parsingExceptions[1].reason)
        Assert.assertEquals(ParsingExceptionReason.DEPENDENCY_FAILED, parsingExceptions[2].reason)
        Assert.assertEquals("Value '[]' for key 'colors' is not valid",
            (parsingExceptions[2].cause as ParsingException).message)
    }
}

private const val TYPE_MISMATCH_AT_FIELD = """
{
    "log_id": "div2_sample_card",
    "states": [
        {
            "state_id": 0,
            "div": {                    
                "type": "container",
                "orientation": "horizontal",
                "items": [
                    {                    
                        "type": "text",
                        "text": 17
                    }
                ]
            }
        }
    ]
}
"""
private const val TYPE_MISMATCH_AT_OPTIONAL_FIELD = """
{
    "log_id": "div2_sample_card",
    "states": [
        {
            "state_id": 0,
            "div": {                    
                "type": "container",
                "orientation": "horizontal",
                "items": [
                    {                    
                        "type": "text",
                        "text": "title",
                        "font_family": 17
                    }
                ]
            }
        }
    ]
}
"""

private const val TYPE_MISMATCH_AT_LIST = """
{
    "log_id": "div2_sample_card",
    "states": [
        {
            "state_id": 0,
            "div": {
                "type": "container",
                "orientation": "horizontal",
                "items": [
                    {
                        "type": "text",
                        "text": "title",
                        "background": [
                            {
                                "type": "gradient",
                                "colors": [
                                    112233,
                                    332211
                                ]
                            }
                        ]
                    }
                ]
            }
        }
    ]
}
"""
