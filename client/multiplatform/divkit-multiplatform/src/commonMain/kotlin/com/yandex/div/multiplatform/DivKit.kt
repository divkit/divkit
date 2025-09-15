package com.yandex.div.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.multiplatform.dependencies.DivKitDependencies

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
