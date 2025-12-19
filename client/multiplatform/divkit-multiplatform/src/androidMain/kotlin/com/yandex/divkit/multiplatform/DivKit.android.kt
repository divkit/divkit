package com.yandex.divkit.multiplatform

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.divkit.multiplatform.dependencies.DivKitDependencies
import org.json.JSONArray
import org.json.JSONObject

internal class DivKitImpl(
    private val variableController: DivVariableController
) : DivKit {

    override fun getVariableValue(name: String): Any? {
        return variableController.get(name)?.getValue()
    }

    override fun setVariable(name: String, value: Any) {
        val variable = asVariable(name, value)
        variable?.let { variableController.putOrUpdate(it) }
    }

    override fun setVariables(variables: Map<String, Any>) {
        variables.forEach { (name, value) ->
            setVariable(name, value)
        }
    }

    private fun asVariable(name: String, value: Any): Variable? {
        return when (value) {
            is Boolean -> Variable.BooleanVariable(name, value)
            is String -> Variable.StringVariable(name, value)
            is Long -> Variable.IntegerVariable(name, value)
            is Double -> Variable.DoubleVariable(name, value)
            is Uri -> Variable.UrlVariable(name, value)
            is Int -> Variable.ColorVariable(name, value)
            is Array<*> -> Variable.ArrayVariable(name, JSONArray(value))
            is List<*> -> Variable.ArrayVariable(name, JSONArray(value.toTypedArray()))
            is JSONArray -> Variable.ArrayVariable(name, value)
            is Map<*, *> -> Variable.DictVariable(name, JSONObject(value))
            is JSONObject -> Variable.DictVariable(name, value)
            else -> null
        }
    }
}

@Deprecated("Use DivKit(DivKitDependencies, DivKitScope.() -> Unit) instead.")
actual fun makeDivKitFactory(
    dependencies: DivKitDependencies
): DivKitScope {
    return DivKitScopeImpl(
        dependencies,
        DivKitAndroidEnvironment.divKitEnvironment
    )
}

@Composable
actual fun DivKit(
    dependencies: DivKitDependencies,
    block: @Composable DivKitScope.() -> Unit
) {
    val environment = LocalDivKitEnvironment.current
    val scope = remember {
        DivKitScopeImpl(
            dependencies,
            environment
        )
    }
    scope.block()
}
