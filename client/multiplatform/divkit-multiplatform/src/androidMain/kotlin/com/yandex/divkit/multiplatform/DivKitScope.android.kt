package com.yandex.divkit.multiplatform

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.shimmer.DivShimmerExtensionHandler
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.divkit.multiplatform.dependencies.ActionHandler
import com.yandex.divkit.multiplatform.dependencies.DivKitDependencies
import com.yandex.divkit.multiplatform.dependencies.ErrorReporter
import com.yandex.divkit.multiplatform.state.DivKitVariableState
import org.json.JSONObject

internal class DivKitScopeImpl(
    private val dependencies: DivKitDependencies,
    private val environment: DivKitEnvironment
): DivKitScope {

    private val variableController: DivVariableController = DivVariableController()

    private var divContext: Div2Context? = environment.baseContext?.let { ctx ->
        makeDivContext(ctx)
    }

    private val divKit: DivKit = DivKitImpl(variableController)

    private fun makeDivContext(baseContext: Context): Div2Context {
        val contextWrapper = when (baseContext) {
            is ContextThemeWrapper -> baseContext
            else -> ContextThemeWrapper(baseContext, android.R.style.Theme_DeviceDefault)
        }

        val imageLoader = environment.imageLoaderFactory(contextWrapper)

        val divConfiguration = DivConfiguration.Builder(imageLoader)
            .actionHandler(DivKitActionHandler(lazy { divKit }, dependencies.actionHandler))
            .extension(DivShimmerExtensionHandler())
            .divVariableController(variableController)
            .build()

        return Div2Context(
            baseContext = contextWrapper,
            configuration = divConfiguration,
            lifecycleOwner = environment.lifecycleOwner
        )
    }

    @Composable
    override fun DivView(
        cardId: String,
        jsonData: String,
        modifier: Modifier
    ) {
        val divContext = this.divContext ?: makeDivContext(LocalContext.current).also {
            this.divContext = it
        }

        val divData = remember(cardId, jsonData) {
            JSONObject(jsonData).asDiv2DataWithTemplates(cardId, dependencies.errorReporter)
        }

        AndroidView(
            factory = { Div2View(divContext) },
            update = { view -> view.setData(divData, DivDataTag(cardId)) },
            modifier = modifier
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
        variables.forEach { (name, value) ->
            divKit.setVariable(name, value)
        }
    }
}

private class DivKitActionHandler(
    private val divKit: Lazy<DivKit>,
    private val actionHandler: ActionHandler?
) : DivActionHandler() {

    override fun handleAction(
        action: DivAction,
        view: DivViewFacade,
        resolver: ExpressionResolver
    ): Boolean {
        if (super.handleAction(action, view, resolver)) {
            return true
        }
        val uri = action.url?.evaluate(resolver) ?: return false
        actionHandler?.handle(divKit.value, uri.toString())
        return true
    }
}

private class ParsingLogger(
    private val cardId: String,
    private val errorReporter: ErrorReporter?
): ParsingErrorLogger {

    override fun logError(e: Exception) {
        e.message?.let {
            errorReporter?.report(cardId, it)
        }
    }
}

private fun JSONObject.asDiv2DataWithTemplates(
    cardId: String,
    errorReporter: ErrorReporter?
): DivData {
    val templates = optJSONObject("templates") ?: JSONObject()
    val card = getJSONObject("card")
    val environment = DivParsingEnvironment(ParsingLogger(cardId, errorReporter)).apply {
        parseTemplates(templates)
    }
    return DivData(environment, card)
}
