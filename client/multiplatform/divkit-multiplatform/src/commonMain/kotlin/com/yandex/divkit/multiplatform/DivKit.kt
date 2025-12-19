package com.yandex.divkit.multiplatform

import androidx.compose.runtime.Composable
import com.yandex.divkit.multiplatform.dependencies.DivKitDependencies

interface DivKit {

    fun getVariableValue(name: String): Any?

    fun setVariable(name: String, value: Any)

    fun setVariables(variables: Map<String, Any>)
}

fun DivKit.setVariables(vararg variables: Pair<String, Any>) {
    variables.forEach { (name, value) ->
        setVariable(name, value)
    }
}

object NoOpDivKit : DivKit {

    override fun getVariableValue(name: String): Any? {
        throw UnsupportedOperationException()
    }

    override fun setVariable(name: String, value: Any) {
        throw UnsupportedOperationException()
    }

    override fun setVariables(variables: Map<String, Any>) {
        throw UnsupportedOperationException()
    }
}

@Deprecated("Use DivKit(DivKitDependencies, DivKitScope.() -> Unit) instead.")
expect fun makeDivKitFactory(
    dependencies: DivKitDependencies = DivKitDependencies()
): DivKitScope

@Composable
expect fun DivKit(
    dependencies: DivKitDependencies,
    block: @Composable DivKitScope.() -> Unit
)
