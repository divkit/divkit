package com.yandex.divkit.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.DivKitKMP.DivKitKMPActionHandlerProtocol
import cocoapods.DivKitKMP.DivKitKMPErrorReporterProtocol
import cocoapods.DivKitKMP.DivKitKMPFacade
import com.yandex.divkit.multiplatform.dependencies.ActionHandler
import com.yandex.divkit.multiplatform.dependencies.DivKitDependencies
import com.yandex.divkit.multiplatform.dependencies.ErrorReporter
import com.yandex.divkit.multiplatform.state.DivKitVariableState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class DivKitScopeImpl(
    dependencies: DivKitDependencies
): DivKitScope {

    private val divKitFacade = DivKitKMPFacade(
        actionHandler = dependencies.actionHandler?.let {
            DivKitActionHandler(
                divKit = lazy { divKit },
                actionHandler = it
            )
        },
        errorReporter = dependencies.errorReporter?.let {
            DivKitErrorReporter(
                errorReporter = it
            )
        }
    )

    private val divKit: DivKit = DivKitImpl(divKitFacade)

    @Composable
    override fun DivView(
        cardId: String,
        jsonData: String,
        modifier: Modifier
    ) {
        val view = remember(cardId, jsonData) {
            divKitFacade.makeDivKitView(jsonString = jsonData, cardId = cardId)
        }

        UIKitView(
            factory = {
                view
            },
            modifier = modifier,
            properties = UIKitInteropProperties(
                isInteractive = true,
                isNativeAccessibilityEnabled = true
            )
        )
    }

    @Composable
    override fun <T : Any> variable(name: String, value: T): DivKitVariableState<T> {
        LaunchedEffect(Unit) {
            divKit.setVariable(name, value)
        }
        return remember(name) {
            DivKitVariableState(divKit, name)
        }
    }

    @Deprecated("Use DivKitScope.variable(name, value) instead.")
    override fun setGlobalVariables(variables: Map<String, Any>) {
        divKit.setVariables(variables)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class DivKitActionHandler(
    private val divKit: Lazy<DivKit>,
    private val actionHandler: ActionHandler
): DivKitKMPActionHandlerProtocol, NSObject() {

    override fun handleActionWithUrl(url: String) {
        actionHandler.handle(divKit.value, url)
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
