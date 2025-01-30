package com.yandex.div.core.view2.local

import android.app.Activity
import android.view.View
import com.yandex.div.BuildConfig
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.util.textString
import com.yandex.div2.DivData
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class SetVariableForLocalVariablesTest {
    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val div2Context = Div2Context(
        baseContext = activity,
        lifecycleOwner = null,
        configuration = DivConfiguration.Builder(mock()).build()
    )
    private val div2View = Div2View(div2Context)

    private val container get() = div2View.getChildAt(0) as DivLinearLayout
    private val globalText get() = container.getChildAt(0) as DivLineHeightTextView
    private val localText get() = container.getChildAt(1) as DivLineHeightTextView
    private val innerContainer get() = container.getChildAt(2) as DivLinearLayout
    private val parentLocalText get() = innerContainer.getChildAt(0) as DivLineHeightTextView

    @Before
    fun setDiv2View() {
        val path = "${BuildConfig.DIV2_JSON_PATH}/regression_test_data/variables/local.json"
        val jsonString = File(path).readText(Charsets.UTF_8)
        val testJson = JSONObject(jsonString)
        val environment = DivParsingEnvironment({ e -> throw AssertionError(e) })

        environment.parseTemplates(testJson.getJSONObject("templates"))
        val divData = DivData(environment, testJson.getJSONObject("card"))

        div2View.setData(divData, DivDataTag("tag"))
    }

    @Test
    fun `variable with card variable shows and updates card variable`() {
        globalText.performClick()

        Assert.assertEquals("global string_var = 'new value'", globalText.textString)
        assertOtherViewsNotChanged(globalText)
    }

    @Test
    fun `variable with local variable shows and updates local variable`() {
        localText.performClick()

        Assert.assertEquals("local string_var = 'new value'", localText.textString)
        assertOtherViewsNotChanged(localText)
    }

    @Test
    fun `variable with parent local variable shows and updates parent local variable`() {
        parentLocalText.performClick()

        Assert.assertEquals("parent local string_var = 'new value'", parentLocalText.textString)
        assertOtherViewsNotChanged(parentLocalText)
    }

    private fun assertOtherViewsNotChanged(view: View?) {
        if (view != globalText) {
            Assert.assertEquals("global string_var = 'global value'", globalText.textString)
        }
        if (view != localText) {
            Assert.assertEquals("local string_var = 'local value'", localText.textString)
        }
        if (view != parentLocalText) {
            Assert.assertEquals("parent local string_var = 'local value'", parentLocalText.textString)
        }
    }
}
