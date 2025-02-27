package com.yandex.div.core.view2

import android.app.Activity
import android.widget.TextView
import com.yandex.div.BuildConfig
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.util.textString
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class SetVariableValueTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val divContext = Div2Context(
        baseContext = activity,
        lifecycleOwner = null,
        configuration = DivConfiguration.Builder(mock()).build()
    )
    private val divView = Div2View(divContext)

    @Before
    fun setUp() {
        val path = "${BuildConfig.DIV2_JSON_PATH}/unit_test_data/variables/set_value.json"
        val testJson = JSONObject(File(path).readText(Charsets.UTF_8))
        val environment = DivParsingEnvironment(ParsingErrorLogger.ASSERT)

        val divData = DivData(environment, testJson.getJSONObject("card"))

        divView.setData(divData, DivDataTag("tag"))
    }

    @Test
    fun `boolean variable updated successfully`() {
        assertNull(divView.setVariable("bool_var", "true"))
        assertEquals(divView.findViewWithTag<TextView>("bool_var_text").textString, "true")
    }

    @Test
    fun `boolean variable update failed due to type mismatch`() {
        assertEquals(
            "Variable 'bool_var' mutation failed!",
            divView.setVariable("bool_var", "new string value")?.message
        )
        assertEquals(divView.findViewWithTag<TextView>("bool_var_text").textString, "false")
    }

    @Test
    fun `integer variable updated successfully`() {
        assertNull(divView.setVariable("int_var", "1"))
        assertEquals(divView.findViewWithTag<TextView>("int_var_text").textString, "1")
    }

    @Test
    fun `integer variable update failed due to type mismatch`() {
        assertEquals(
            "Variable 'int_var' mutation failed!",
            divView.setVariable("int_var", "new string value")?.message
        )
        assertEquals(divView.findViewWithTag<TextView>("int_var_text").textString, "0")
    }

    @Test
    fun `number variable updated successfully`() {
        assertNull(divView.setVariable("number_var", "1.1"))
        assertEquals(divView.findViewWithTag<TextView>("number_var_text").textString, "1.1")
    }

    @Test
    fun `number variable update failed due to type mismatch`() {
        assertEquals(
            "Variable 'number_var' mutation failed!",
            divView.setVariable("number_var", "new string value")?.message
        )
        assertEquals(divView.findViewWithTag<TextView>("number_var_text").textString, "0.1")
    }

    @Test
    fun `string variable updated successfully`() {
        assertNull(divView.setVariable("string_var", "new value"))
        assertEquals(divView.findViewWithTag<TextView>("string_var_text").textString, "new value")
    }

    @Test
    fun `color variable updated successfully`() {
        assertNull(divView.setVariable("color_var", "#FFFFFF"))
        assertEquals(divView.findViewWithTag<TextView>("color_var_text").textString, "#FFFFFFFF")
    }

    @Test
    fun `color variable update failed due to type mismatch`() {
        assertEquals(
            "Variable 'color_var' mutation failed!",
            divView.setVariable("color_var", "new string value")?.message
        )
        assertEquals(divView.findViewWithTag<TextView>("color_var_text").textString, "#FF000000")
    }

    @Test
    fun `url variable updated successfully`() {
        assertNull(divView.setVariable("url_var", "https://new/url"))
        assertEquals(divView.findViewWithTag<TextView>("url_var_text").textString, "https://new/url")
    }

    @Test
    fun `dict variable updated successfully`() {
        assertNull(divView.setVariable("dict_var", "{\"value\":\"new value\"}"))
        assertEquals(divView.findViewWithTag<TextView>("dict_var_text").textString, "{\"value\":\"new value\"}")
    }

    @Test
    fun `dict variable update failed due to type mismatch`() {
        assertEquals(
            "Variable 'dict_var' mutation failed!",
            divView.setVariable("dict_var", "new string value")?.message
        )
        assertEquals(divView.findViewWithTag<TextView>("dict_var_text").textString, "{\"value\":\"initial value\"}")
    }

    @Test
    fun `array variable updated successfully`() {
        assertNull(divView.setVariable("array_var", "[\"new value\"]"))
        assertEquals(divView.findViewWithTag<TextView>("array_var_text").textString, "[\"new value\"]")
    }

    @Test
    fun `array variable update failed due to type mismatch`() {
        assertEquals(
            "Variable 'array_var' mutation failed!",
            divView.setVariable("array_var", "new string value")?.message
        )
        assertEquals(divView.findViewWithTag<TextView>("array_var_text").textString, "[\"initial value\"]")
    }
}
