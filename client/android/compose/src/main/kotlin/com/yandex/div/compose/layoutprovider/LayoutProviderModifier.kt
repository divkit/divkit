package com.yandex.div.compose.layoutprovider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.DivLayoutProvider

@Composable
internal fun Modifier.layoutProvider(layoutProvider: DivLayoutProvider): Modifier {
    val widthVariableName = layoutProvider.widthVariableName
    val heightVariableName = layoutProvider.heightVariableName
    if (widthVariableName.isNullOrEmpty() && heightVariableName.isNullOrEmpty()) {
        reportError("Neither width_variable_name nor height_variable_name found")
        return this
    }

    val storage = LocalDivViewContext.current.component.layoutProviderStorage
    val expressionResolver = LocalComponent.current.expressionResolver
    val widthVariable = remember(widthVariableName, expressionResolver) {
        widthVariableName?.let { storage.getVariableHandler(it, expressionResolver) }
    }
    val heightVariable = remember(heightVariableName, expressionResolver) {
        heightVariableName?.let { storage.getVariableHandler(it, expressionResolver) }
    }
    DisposableEffect(widthVariable, heightVariable) {
        widthVariable?.register()
        heightVariable?.register()
        onDispose {
            widthVariable?.unregister()
            heightVariable?.unregister()
        }
    }

    val density = LocalDensity.current.density
    val onSizeChanged: (IntSize) -> Unit =
        remember(widthVariable, heightVariable, density) {
            { size ->
                widthVariable?.update(sizeInPixels = size.width, density = density)
                heightVariable?.update(sizeInPixels = size.height, density = density)
            }
        }
    return onSizeChanged(onSizeChanged)
}
