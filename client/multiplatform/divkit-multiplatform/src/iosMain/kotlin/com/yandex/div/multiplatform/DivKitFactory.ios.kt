package com.yandex.div.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.DivKitKMP.DivKitKMPFacade
import cocoapods.DivKitKMP.DivKitKMPActionHandlerProtocol
import cocoapods.DivKitKMP.DivKitKMPErrorReporterProtocol
import com.yandex.div.multiplatform.dependencies.ActionHandler
import com.yandex.div.multiplatform.dependencies.DivKitDependencies
import com.yandex.div.multiplatform.dependencies.ErrorReporter
import kotlinx.cinterop.ExperimentalForeignApi
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
        UIKitView(
            factory = {
                divKitFactory.makeDivKitView(jsonString = jsonData, cardId = cardId)
            },
            modifier = modifier,
            properties = UIKitInteropProperties(
                isInteractive = true,
                isNativeAccessibilityEnabled = true
            )
        )
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
