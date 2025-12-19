package com.yandex.divkit.multiplatform

import androidx.compose.runtime.Composable
import cocoapods.DivKitKMP.DivKitKMPFacade
import com.yandex.divkit.multiplatform.dependencies.DivKitDependencies
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
internal class DivKitImpl(
    private val divKitFacade: DivKitKMPFacade
) : DivKit {

    override fun getVariableValue(name: String): Any? {
        return divKitFacade.getVariableValue(name)
    }

    override fun setVariable(name: String, value: Any) {
        setVariables(mapOf(name to value))
    }

    override fun setVariables(variables: Map<String, Any>) {
        divKitFacade.setGlobalVariables(
            variables = variables.entries.associate { (key, value) ->
                key as Any? to value
            }
        )
    }
}

@Deprecated("Use DivKit(DivKitDependencies, DivKitScope.() -> Unit) instead.")
actual fun makeDivKitFactory(
    dependencies: DivKitDependencies
): DivKitScope {
    return DivKitScopeImpl(dependencies)
}

@Composable
actual fun DivKit(
    dependencies: DivKitDependencies,
    block: @Composable DivKitScope.() -> Unit
) {
    val scope = DivKitScopeImpl(dependencies)
    scope.block()
}
