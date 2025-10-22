package com.yandex.divkit.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.divkit.multiplatform.dependencies.DivKitDependencies

interface DivKitFactory {
    @Composable
    fun DivView(
        cardId: String,
        jsonData: String,
        modifier: Modifier = Modifier
    )

    fun setGlobalVariables(variables: Map<String, Any>)
}

expect fun makeDivKitFactory(
    dependencies: DivKitDependencies = DivKitDependencies()
): DivKitFactory
