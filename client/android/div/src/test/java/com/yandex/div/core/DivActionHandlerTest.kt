package com.yandex.div.core

import android.app.Activity
import android.net.Uri
import com.yandex.div.DivDataTag
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.disableAssertions
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionArrayInsertValue
import com.yandex.div2.DivActionArrayRemoveValue
import com.yandex.div2.DivActionArraySetValue
import com.yandex.div2.DivActionDictSetValue
import com.yandex.div2.DivActionSetVariable
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivContainer
import com.yandex.div2.DivData
import com.yandex.div2.DivTypedValue
import com.yandex.div2.IntegerValue
import com.yandex.div2.StrValue
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivActionHandlerTest {

    private val divView = spy(
        Div2View(
            Div2Context(
                baseContext = Robolectric.buildActivity(Activity::class.java).get(),
                configuration = DivConfiguration.Builder(mock()).build(),
                lifecycleOwner = null
            )
        )
    )

    private val underTest = DivActionHandler()

    init {
        divView.setData(
            DivData("id", listOf(DivData.State(Div.Container(DivContainer()), 0))),
            DivDataTag("id")
        )
    }

    @Test
    fun `uri with numeric state is handled`() {
        val isHandled = handleActionUrl("div-action://set_state?state_id=1")

        verify(divView).switchToState("1".path, true)
        Assert.assertTrue(isHandled)
    }

    @Test
    fun `uri with string state is handled`() {
        val isHandled = handleActionUrl("div-action://set_state?state_id=1/foo/bar&temporary=true")

        verify(divView).switchToState("1/foo/bar".path, true)
        Assert.assertTrue(isHandled)
    }

    @Test
    fun `uri with escaped parameter is handled`() {
        val isHandled = handleActionUrl("div-action://set_state?state_id=1%2Ffoo%2Fbar%2Flol%2Fkek")

        verify(divView).switchToState("1/foo/bar/lol/kek".path, true)
        Assert.assertTrue(isHandled)
    }

    @Test
    fun `uri without path is not handled`() {
        val isHandled = handleActionUrl("div-action://?state_id=1")

        Assert.assertFalse(isHandled)
    }

    @Test
    fun `uri with invalid path is not handled`() {
        disableAssertions {
            Assert.assertFalse(handleActionUrl("div-action://set_state?state_id=1.5/foo/bar/lol/kek"))
            Assert.assertFalse(handleActionUrl("div-action://set_state?state_id=1/foo/bar/lol"))
        }
    }

    @Test
    fun `SetVariable action updates variable`() {
        setVariable("string_var", "value")

        val isHandled = handleTypedAction(
            DivActionTyped.SetVariable(
                DivActionSetVariable(
                    value = typedValue("new value"),
                    variableName = Expression.constant("string_var")
                )
            )
        )

        Assert.assertTrue(isHandled)
        Assert.assertEquals("new value", getVariableValue("string_var"))
    }

    @Test
    fun `SetVariable action does not add new variable`() {
        val isHandled = handleTypedAction(
            DivActionTyped.SetVariable(
                DivActionSetVariable(
                    value = typedValue("new value"),
                    variableName = Expression.constant("string_var")
                )
            )
        )

        Assert.assertTrue(isHandled)
        Assert.assertNull(getVariableValue("string_var"))
    }

    @Test
    fun `SetVariable action does not update different type variable`() {
        setVariable("string_var", "value")

        val isHandled = handleTypedAction(
            DivActionTyped.SetVariable(
                DivActionSetVariable(
                    value = typedValue(123),
                    variableName = Expression.constant("string_var")
                )
            )
        )

        Assert.assertTrue(isHandled)
        Assert.assertEquals("value", getVariableValue("string_var"))
    }

    @Test
    fun `ArrayInsertValue action without index inserts value`() {
        setVariable("array_var", JSONArray().put("value 1").put("value 2"))

        val isHandled = handleTypedAction(
            DivActionTyped.ArrayInsertValue(
                DivActionArrayInsertValue(
                    value = typedValue("new value"),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertTrue(isHandled)

        Assert.assertEquals(
            JSONArray().put("value 1").put("value 2").put("new value"),
            getVariableValue("array_var")
        )
    }

    @Test
    fun `ArrayInsertValue action with index inserts value`() {
        setVariable("array_var", JSONArray().put("value 1").put("value 2"))

        val isHandled = handleTypedAction(
            DivActionTyped.ArrayInsertValue(
                DivActionArrayInsertValue(
                    index = Expression.constant(1),
                    value = typedValue("new value"),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertTrue(isHandled)

        Assert.assertEquals(
            JSONArray().put("value 1").put("new value").put("value 2"),
            getVariableValue("array_var")
        )
    }

    @Test
    fun `ArrayInsertValue action with index equal to length inserts value`() {
        setVariable("array_var", JSONArray().put("value 1"))

        val isHandled = handleTypedAction(
            DivActionTyped.ArrayInsertValue(
                DivActionArrayInsertValue(
                    index = Expression.constant(1),
                    value = typedValue("new value"),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertTrue(isHandled)

        Assert.assertEquals(
            JSONArray().put("value 1").put("new value"),
            getVariableValue("array_var")
        )
    }

    @Test
    fun `ArrayInsertValue action does not change original value`() {
        val array = JSONArray().put("value 1").put("value 2")
        setVariable("array_var", array)

        handleTypedAction(
            DivActionTyped.ArrayInsertValue(
                DivActionArrayInsertValue(
                    value = typedValue("new value"),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertEquals(JSONArray().put("value 1").put("value 2"), array)
    }

    @Test
    fun `ArrayInsertValue action does nothing for invalid index`() {
        setVariable("array_var", JSONArray().put("value 1"))

        val isHandled = handleTypedAction(
            DivActionTyped.ArrayInsertValue(
                DivActionArrayInsertValue(
                    index = Expression.constant(2),
                    value = typedValue("new value"),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertTrue(isHandled)
        Assert.assertEquals(JSONArray().put("value 1"), getVariableValue("array_var"))
    }

    @Test
    fun `ArrayInsertValue action does nothing for not array variable`() {
        setVariable("string_var", "value")

        val isHandled = handleTypedAction(
            DivActionTyped.ArrayInsertValue(
                DivActionArrayInsertValue(
                    value = typedValue("new value"),
                    variableName = Expression.constant("string_var")
                )
            )
        )

        Assert.assertTrue(isHandled)
        Assert.assertEquals("value", getVariableValue("string_var"))
    }

    @Test
    fun `ArrayRemoveValue action removes value`() {
        setVariable("array_var", JSONArray().put("value 1").put("value 2"))

        val isHandled = handleTypedAction(
            DivActionTyped.ArrayRemoveValue(
                DivActionArrayRemoveValue(
                    index = Expression.constant(0),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertTrue(isHandled)
        Assert.assertEquals(JSONArray().put("value 2"), getVariableValue("array_var"))
    }

    @Test
    fun `ArrayRemoveValue action does not change original array`() {
        val array = JSONArray().put("value 1").put("value 2")
        setVariable("array_var", array)

        handleTypedAction(
            DivActionTyped.ArrayRemoveValue(
                DivActionArrayRemoveValue(
                    index = Expression.constant(0),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertEquals(JSONArray().put("value 1").put("value 2"), array)
    }

    @Test
    fun `ArrayRemoveValue action does nothing for invalid index`() {
        setVariable("array_var", JSONArray().put("value 1"))

        val isHandled = handleTypedAction(
            DivActionTyped.ArrayRemoveValue(
                DivActionArrayRemoveValue(
                    index = Expression.constant(2),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertTrue(isHandled)
        Assert.assertEquals(JSONArray().put("value 1"), getVariableValue("array_var"))
    }

    @Test
    fun `ArrayRemoveValue action does nothing for not array variable`() {
        setVariable("string_var", "value")

        val isHandled = handleTypedAction(
            DivActionTyped.ArrayRemoveValue(
                DivActionArrayRemoveValue(
                    index = Expression.constant(2),
                    variableName = Expression.constant("string_var")
                )
            )
        )

        Assert.assertTrue(isHandled)
        Assert.assertEquals("value", getVariableValue("string_var"))
    }

    @Test
    fun `ArraySetValue action sets value`() {
        setVariable("array_var", JSONArray().put("value 1").put("value 2"))

        val isHandled = handleTypedAction(
            DivActionTyped.ArraySetValue(
                DivActionArraySetValue(
                    index = Expression.constant(1),
                    value = typedValue("new value"),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertTrue(isHandled)

        Assert.assertEquals(
            JSONArray().put("value 1").put("new value"),
            getVariableValue("array_var")
        )
    }

    @Test
    fun `ArraySetValue action does not change original array`() {
        val array = JSONArray().put("value 1").put("value 2")
        setVariable("array_var", array)

        handleTypedAction(
            DivActionTyped.ArraySetValue(
                DivActionArraySetValue(
                    index = Expression.constant(1),
                    value = typedValue("new value"),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertEquals(JSONArray().put("value 1").put("value 2"), array)
    }

    @Test
    fun `ArraySetValue action does nothing for invalid index`() {
        setVariable("array_var", JSONArray().put("value 1").put("value 2"))

        val isHandled = handleTypedAction(
            DivActionTyped.ArraySetValue(
                DivActionArraySetValue(
                    index = Expression.constant(2),
                    value = typedValue("new value"),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertTrue(isHandled)

        Assert.assertEquals(
            JSONArray().put("value 1").put("value 2"),
            getVariableValue("array_var")
        )
    }

    @Test
    fun `ArraySetValue action does nothing for not array variable`() {
        setVariable("string_var", "value")

        val isHandled = handleTypedAction(
            DivActionTyped.ArraySetValue(
                DivActionArraySetValue(
                    index = Expression.constant(0),
                    value = typedValue("new value"),
                    variableName = Expression.constant("string_var")
                )
            )
        )

        Assert.assertTrue(isHandled)
        Assert.assertEquals("value", getVariableValue("string_var"))
    }

    @Test
    fun `DictSetValue action removes value`() {
        setVariable("dict_var", JSONObject().put("key", "value"))

        val isHandled = handleTypedAction(
            DivActionTyped.DictSetValue(
                DivActionDictSetValue(
                    key = Expression.constant("key"),
                    variableName = Expression.constant("dict_var")
                )
            )
        )

        Assert.assertTrue(isHandled)
        assertEquals(JSONObject(), getVariableValue("dict_var"))
    }

    @Test
    fun `DictSetValue action adds value`() {
        setVariable("dict_var", JSONObject().put("key", "value"))

        val isHandled = handleTypedAction(
            DivActionTyped.DictSetValue(
                DivActionDictSetValue(
                    key = Expression.constant("new key"),
                    value = typedValue(123),
                    variableName = Expression.constant("dict_var")
                )
            )
        )

        Assert.assertTrue(isHandled)

        assertEquals(
            JSONObject().put("key", "value").put("new key", 123),
            getVariableValue("dict_var")
        )
    }

    @Test
    fun `DictSetValue action updates value`() {
        setVariable("dict_var", JSONObject().put("key", "value"))

        val isHandled = handleTypedAction(
            DivActionTyped.DictSetValue(
                DivActionDictSetValue(
                    key = Expression.constant("key"),
                    value = typedValue(123),
                    variableName = Expression.constant("dict_var")
                )
            )
        )

        Assert.assertTrue(isHandled)

        assertEquals(
            JSONObject().put("key", 123),
            getVariableValue("dict_var")
        )
    }

    @Test
    fun `DictSetValue action does nothing for not dict variable`() {
        setVariable("array_var", JSONArray().put("value"))

        val isHandled = handleTypedAction(
            DivActionTyped.DictSetValue(
                DivActionDictSetValue(
                    key = Expression.constant("key"),
                    value = typedValue(123),
                    variableName = Expression.constant("array_var")
                )
            )
        )

        Assert.assertTrue(isHandled)
        Assert.assertEquals(JSONArray().put("value"), getVariableValue("array_var"))
    }

    @Test
    fun `DictSetValue action does not change original value`() {
        val dict = JSONObject().put("key", "value")
        setVariable("dict_var", dict)

        handleTypedAction(
            DivActionTyped.DictSetValue(
                DivActionDictSetValue(
                    key = Expression.constant("key"),
                    value = typedValue("new value"),
                    variableName = Expression.constant("dict_var")
                )
            )
        )

        assertEquals(JSONObject().put("key", "value"), dict)
    }

    private val String.path: DivStatePath get() = DivStatePath.parse(this)

    private fun assertEquals(expected: JSONObject, actual: Any?) {
        Assert.assertEquals(expected.toString(), actual.toString())
    }

    private fun handleActionUrl(uri: String): Boolean {
        return underTest.handleActionUrl(Uri.parse(uri), divView, divView.expressionResolver)
    }

    private fun handleTypedAction(action: DivActionTyped): Boolean {
        return underTest.handleAction(
            DivAction(
                logId = Expression.constant("log_id"),
                typed = action
            ),
            divView,
            divView.expressionResolver
        )
    }

    private fun getVariableValue(name: String): Any? {
        return divView.context.divVariableController.get(name)?.getValue()
    }

    private fun setVariable(name: String, value: String) {
        divView.context.divVariableController.putOrUpdate(
            Variable.StringVariable(name = name, defaultValue = value)
        )
    }

    private fun setVariable(name: String, value: JSONArray) {
        divView.context.divVariableController.putOrUpdate(
            Variable.ArrayVariable(name = name, defaultValue = value)
        )
    }

    private fun setVariable(name: String, value: JSONObject) {
        divView.context.divVariableController.putOrUpdate(
            Variable.DictVariable(name = name, defaultValue = value)
        )
    }

    private fun typedValue(value: String): DivTypedValue {
        return DivTypedValue.Str(StrValue(Expression.constant(value)))
    }

    private fun typedValue(value: Long): DivTypedValue {
        return DivTypedValue.Integer(IntegerValue(Expression.constant(value)))
    }
}
