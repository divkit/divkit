package com.yandex.divkit.multiplatform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.DivKitKMP.DivKitKMPActionHandlerProtocol
import cocoapods.DivKitKMP.DivKitKMPErrorReporterProtocol
import cocoapods.DivKitKMP.DivKitKMPFacade
import com.yandex.divkit.multiplatform.dependencies.ActionHandler
import com.yandex.divkit.multiplatform.dependencies.DivKitDependencies
import com.yandex.divkit.multiplatform.dependencies.ErrorReporter
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGSizeMake
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class DivKitFactoryImpl(
    dependencies: DivKitDependencies
): DivKitFactory {
    private val divKitFactory = DivKitKMPFacade(
        actionHandler = dependencies.actionHandler?.let {
            DivKitActionHandler(
                actionHandler = it
            )
        },
        errorReporter = dependencies.errorReporter?.let {
            DivKitErrorReporter(
                errorReporter = it
            )
        }
    )

    @Composable
    override fun DivView(
        cardId: String,
        jsonData: String,
        modifier: Modifier
    ) {
        val view = remember(cardId, jsonData) {
            divKitFactory.makeDivKitView(jsonString = jsonData, cardId = cardId)
        }

        Box(modifier = modifier) {
            UIKitView(
                factory = {
                    view
                },
                modifier = Modifier.fillMaxSize(),
                properties = UIKitInteropProperties(
                    isInteractive = true,
                    isNativeAccessibilityEnabled = true
                )
            )
        }
    }

    override fun setGlobalVariables(variables: Map<String, Any>) {
        divKitFactory.setGlobalVariables(
            variables = variables.entries.associate { (key, value) ->
                key as Any? to value
            }
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private class DivKitActionHandler(
    private val actionHandler: ActionHandler
): DivKitKMPActionHandlerProtocol, NSObject() {
    override fun handleActionWithUrl(url: String) {
        actionHandler.handle(url)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class DivKitErrorReporter(
    private val errorReporter: ErrorReporter
): DivKitKMPErrorReporterProtocol, NSObject() {
    override fun reportWithCardId(cardId: String, message: String) {
        errorReporter.report(cardId, message)
    }
}
