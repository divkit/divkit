package com.yandex.div.core.view2

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.Assert
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivPatchApplyItemsTest {
    private val divImageLoader = mock<DivImageLoader>()
    private val controller = Robolectric.buildActivity(Activity::class.java)
    private val activity = controller.get()
    private val env = DivParsingEnvironment(
        logger = { Assert.fail("Unepected parsing error!", it) }
    )

    private val div2Context = Div2Context(
        activity,
        DivConfiguration.Builder(divImageLoader).build()
    )

    private val div2View = Div2View(div2Context)
    private val tag = DivDataTag("tag")

    @Test
    fun `(container) patch does not add empty items if they weren't specified`() {
        testContainer(containerType = "container")
    }

    @Test
    fun `(grid) patch does not add empty items if they weren't specified`() {
        testContainer(containerType = "grid")
    }

    @Test
    fun `(gallery) patch does not add empty items if they weren't specified`() {
        testContainer(containerType = "gallery")
    }

    @Test
    fun `(custom) patch does not add empty items if they weren't specified`() {
        testContainer(containerType = "custom")
    }

    private fun testContainer(containerType: String) {
        val initialData = DivData(env, JSONObject(createInitialDataWith(containerType)))
        div2View.setData(initialData, tag)
        Assert.assertNull(div2View.rootDivContainerItems())

        Assert.assertTrue(div2View.applyPatch(DivPatch(env, JSONObject(DIV_PATCH))))

        Assert.assertNull(div2View.rootDivContainerItems())
    }
}

private fun Div2View.rootDivContainerItems(): List<Div?>? {
    val div = divData!!.states.first().div
    return when (div) {
        is Div.Container -> div.value.items
        is Div.Grid -> div.value.items
        is Div.Gallery -> div.value.items
        is Div.Pager -> div.value.items
        is Div.Custom -> div.value.items
        else -> throw RuntimeException("Unsupported type: $div")
    }
}

private fun createInitialDataWith(containerType: String) = """
{
    "log_id": "_",
    "states": [
        {
            "state_id": 0,
            "div": {
                "type": "$containerType",
                "column_count": 1,
                "custom_type": "custom",
                "width": {
                    "type": "match_parent"
                }
            }
        }
    ]
}
"""

private const val DIV_PATCH = """
{
    "mode": "partial",
    "changes": [
        {
            "id": "absent",
            "items": []
        }
    ]
}   
"""
