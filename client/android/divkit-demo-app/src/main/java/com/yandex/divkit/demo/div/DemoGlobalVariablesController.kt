package com.yandex.divkit.demo.div

import androidx.annotation.MainThread
import com.yandex.div.core.Div2Context
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.expression.variables.DivVariablesParser
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableMutationException
import com.yandex.div.json.ParsingErrorLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DemoGlobalVariablesController {
    private var lastDivVariableController: DivVariableController? = null
    private val globalVariables = mutableListOf<Variable>()

    fun bindWith(context: Div2Context) {
        lastDivVariableController = context.divVariableController
        dispatchVariableChanged()
    }

    private fun dispatchVariableChanged() {
        gatherGlobalVariables().iterator().forEach {
            if (lastDivVariableController?.isDeclared(it.name) == false) {
                lastDivVariableController?.declare(it)
            } else {
                val variable = lastDivVariableController?.get(it.name) ?: return
                variable.set(it.getValue().toString())
            }
        }
    }

    @MainThread
    @Throws(VariableMutationException::class)
    fun declare(variable: Variable) {
        lastDivVariableController?.declare(variable)
    }

    private fun gatherGlobalVariables(): Array<Variable> = globalVariables
        .toTypedArray()

    suspend fun updateVariables(json: JSONObject, logger: ParsingErrorLogger) {
        withContext(Dispatchers.IO) {
            val variables = DivVariablesParser.parse(json.getJSONArray("variables"), logger)
            globalVariables.clear()
            globalVariables.addAll(variables)
        }

        withContext(Dispatchers.Main) {
            dispatchVariableChanged()
        }
    }
}
