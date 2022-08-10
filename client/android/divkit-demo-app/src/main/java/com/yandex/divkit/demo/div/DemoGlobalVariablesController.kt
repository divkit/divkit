package com.yandex.divkit.demo.div

import androidx.annotation.MainThread
import com.yandex.div.core.Div2Context
import com.yandex.div.core.expression.variables.DivVariablesParser
import com.yandex.div.core.expression.variables.GlobalVariableController
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableMutationException
import com.yandex.div.json.ParsingErrorLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DemoGlobalVariablesController {
    private var lastGlobalVariableController: GlobalVariableController? = null
    private val globalVariables = mutableListOf<Variable>()

    fun bindWith(context: Div2Context) {
        lastGlobalVariableController = context.div2Component.globalVariableController
        dispatchVariableChanged()
    }

    private fun dispatchVariableChanged() {
        lastGlobalVariableController?.putOrUpdate(*gatherGlobalVariables())
    }

    @MainThread
    @Throws(VariableMutationException::class)
    fun putOrUpdate(variable: Variable) {
        lastGlobalVariableController?.putOrUpdate(variable)
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
