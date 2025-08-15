package com.yandex.div.core.view2.errors

import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableMutationException

private typealias VariableWithPath = Pair<String, Variable>
private typealias PathAndName = Pair<String, String>
private typealias VariablesUpdatedCallback = (List<VariableWithPath>) -> Unit

internal class VariableMonitor(
    private val errorHandler: (Throwable) -> Unit
) {

    private val variables = mutableMapOf<PathAndName, Variable>()
    private var variablesUpdatedCallback: VariablesUpdatedCallback? = null

    var controllerMap: Map<String, VariableController> = emptyMap()
        set(value) {
            if (hasAllPairs(field, value)) return
            val oldControllers = field.values.toSet()
            field = value
            onControllersChange(oldControllers)
        }

    fun mutateVariable(name: String, path: String, value: String) {
        val variable = variables[path to name]
        if (variable?.getValue().toString() == value) return

        try {
            variable?.set(value)
        } catch (e: Exception) {
            errorHandler.invoke(VariableMutationException("Unable to set '$value' value to variable '$name'."))
        }
    }

    fun setVariablesUpdatedCallback(callback: VariablesUpdatedCallback) {
        variablesUpdatedCallback = callback
        notifyOnChange()
    }

    private fun onControllersChange(oldControllers: Set<VariableController>) {
        controllerMap
            .filter { (_, controller) -> controller !in oldControllers }
            .forEach { (path, controller) ->
                controller.subscribeToVariablesChange(
                    controller.getAllNames(),
                    observer = createCallback(path)
                )
            }

        variables.clear()
        controllerMap.forEach { (path, controller) ->
            controller.captureAll().forEach { variable ->
                saveVariable(variable, path)
            }
        }
        notifyOnChange()
    }

    private fun createCallback(path: String) = { variable: Variable ->
        saveVariable(variable, path)
        notifyOnChange()
    }

    private fun notifyOnChange() {
        val list = variablesList()
        variablesUpdatedCallback?.invoke(list)
    }
    
    private fun saveVariable(variable: Variable, path: String) {
        variables[path to variable.name] = variable
    }

    private fun variablesList(): List<VariableWithPath> {
        return variables.map(::entriesToVariables)
            .sortedBy { (path, variable) -> path + variable.name }
    }

    private fun entriesToVariables(entry: Map.Entry<PathAndName, Variable>): VariableWithPath {
        val (pathAndName, variable) = entry
        return pathAndName.first to variable
    }

    private fun <K, V> hasAllPairs(map: Map<K, V>, from: Map<K, V>): Boolean {
        return from.all { (k, v) -> map[k] == v }
    }

    private fun VariableController.getAllNames(): List<String> {
        return captureAll().map { it.name }
    }

}
