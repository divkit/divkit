package com.yandex.divkit.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.divkit.multiplatform.state.DivKitVariableState

interface DivKitScope {

    @Composable
    fun DivView(
        cardId: String,
        jsonData: String,
        modifier: Modifier = Modifier
    )

    @Composable
    fun <T : Any> variable(name: String, value: T): DivKitVariableState<T>

    @Deprecated("Use DivKitScope.variable(name, value) instead.")
    fun setGlobalVariables(variables: Map<String, Any>)
}

@Deprecated("Use DivKitScope instead.")
interface DivKitFactory : DivKitScope
