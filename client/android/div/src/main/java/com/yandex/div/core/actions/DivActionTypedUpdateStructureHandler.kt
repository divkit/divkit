package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivActionUpdateStructure
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedUpdateStructureHandler @Inject constructor() : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean = when (action) {
        is DivActionTyped.UpdateStructure -> handleAction(action.value, view, resolver)
        else -> false
    }

    private fun handleAction(
        action: DivActionUpdateStructure,
        divView: Div2View,
        resolver: ExpressionResolver
    ): Boolean {
        val variableName = action.variableName.evaluate(resolver)
        val path = action.path.evaluate(resolver)
        val pathSegments = path.split("/").filter { it.isNotEmpty() }
        val newValue = action.value.evaluate(resolver)

        if (path.isNotEmpty() && pathSegments.isEmpty()) {
            divView.logError(RuntimeException("Malformed path '$path': all path segments are empty"))
            return true
        }

        VariableMutationHandler.setVariable(divView, variableName, resolver) { variable: Variable ->
            when (variable) {
                is Variable.ArrayVariable -> updateArrayStructure(divView, variable, pathSegments, newValue)
                is Variable.DictVariable -> updateDictStructure(divView, variable, pathSegments, newValue)
                else -> divView.logError(RuntimeException("Action requires array or dictionary variable"))
            }
            return@setVariable variable
        }
        return true
    }

    private fun updateArrayStructure(
        divView: Div2View,
        variable: Variable.ArrayVariable,
        pathSegments: List<String>,
        newValue: Any
    ) {
        val variableValue = variable.getValue() as JSONArray
        val target = findStructureElement(divView, Structure.Array(variableValue), pathSegments.dropLast(1))
        if (target != null && setValue(divView, target, pathSegments.last(), newValue)) {
            variable.set(variableValue)
        }
    }

    private fun updateDictStructure(
        divView: Div2View,
        variable: Variable.DictVariable,
        pathSegments: List<String>,
        newValue: Any
    ) {
        val variableValue = variable.getValue() as JSONObject
        val target = findStructureElement(divView, Structure.Dictionary(variableValue), pathSegments.dropLast(1))
        if (target != null && setValue(divView, target, pathSegments.last(), newValue)) {
            variable.set(variableValue)
        }
    }

    private fun findStructureElement(divView: Div2View, root: Structure, pathSegments: List<String>): Structure? {
        var target: Structure = root
        pathSegments.forEachIndexed { index, pathElement ->
            target = try {
                when (val structureElement = target.get(pathElement)) {
                    Structure.NonStructure -> {
                        val errorPath = pathSegments.take(index + 1).joinToString(separator = "/")
                        divView.logError(RuntimeException("Element with path '$errorPath' is not a structure"))
                        return null
                    }

                    null -> {
                        val errorPath = pathSegments.take(index + 1).joinToString(separator = "/")
                        divView.logError(RuntimeException("Element with path '$errorPath' is not found"))
                        return null
                    }

                    else -> structureElement
                }
            } catch (e: NumberFormatException) {
                divView.logError(RuntimeException("Unable to use '$pathElement' as array index", e))
                return null
            }
        }
        return target
    }

    private fun setValue(
        divView: Div2View,
        target: Structure,
        pathSegment: String,
        newValue: Any
    ): Boolean {
        try {
            target.set(pathSegment, newValue)
            return true
        } catch (e: NumberFormatException) {
            divView.logError(RuntimeException("Unable to use '$pathSegment' as array index", e))
            return false
        } catch (e: IndexOutOfBoundsException) {
            divView.logError(RuntimeException("Position '$pathSegment' is out of array bounds", e))
            return false
        }
    }

    private sealed interface Structure {

        val size: Int

        fun get(key: String): Structure?

        fun set(key: String, value: Any)

        class Array(private val array: JSONArray) : Structure {

            override val size: Int
                get() = array.length()

            override fun get(key: String): Structure? {
                val index = key.toInt()
                return when (val value = array.opt(index)) {
                    is JSONArray -> Array(value)
                    is JSONObject -> Dictionary(value)
                    null, JSONObject.NULL -> null
                    else -> NonStructure
                }
            }

            override fun set(key: String, value: Any) {
                val index = key.toInt()
                if (index < 0 || index > size) {
                    throw IndexOutOfBoundsException()
                }
                runCatching {
                    array.put(index, value)
                }
            }
        }

        class Dictionary(private val dictionary: JSONObject) : Structure {

            override val size: Int
                get() = dictionary.length()

            override fun get(key: String): Structure? {
                return when (val value = dictionary.opt(key)) {
                    is JSONArray -> Array(value)
                    is JSONObject -> Dictionary(value)
                    null, JSONObject.NULL -> null
                    else -> NonStructure
                }
            }

            override fun set(key: String, value: Any) {
                runCatching {
                    dictionary.put(key, value)
                }
            }
        }

        object NonStructure : Structure {

            override val size: Int
                get() = throw UnsupportedOperationException()

            override fun get(key: String): Structure? {
                throw UnsupportedOperationException()
            }

            override fun set(key: String, value: Any) {
                throw UnsupportedOperationException()
            }
        }
    }
}
