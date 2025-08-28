package com.yandex.div.multiplatform

import android.net.Uri
import android.view.ContextThemeWrapper
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.data.Variable
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.shimmer.DivShimmerExtensionHandler
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div.multiplatform.dependencies.ActionHandler
import com.yandex.div.multiplatform.dependencies.DivKitDependencies
import com.yandex.div.multiplatform.dependencies.ErrorReporter
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class DivKitFactoryImpl(
    private val dependencies: DivKitDependencies,
    private val environment: DivKitEnvironment
): DivKitFactory {
    private val variableController: DivVariableController = DivVariableController()

    @Composable
    override fun DivView(
        cardId: String,
        jsonData: String,
        modifier: Modifier
    ) {
        AndroidView(
            factory = { ctx ->
                val divJson = JSONObject(jsonData)

                val divConfiguration = DivConfiguration.Builder(environment.imageLoaderFactory(ctx))
                    .actionHandler(ComposeActionHandler(dependencies.actionHandler))
                    .extension(DivShimmerExtensionHandler())
                    .divVariableController(variableController)
                    .build()

                val contextWrapper = when (ctx) {
                    is ContextThemeWrapper -> ctx
                    else -> ContextThemeWrapper(ctx, android.R.style.Theme_DeviceDefault)
                }

                val divContext = Div2Context(
                    baseContext = contextWrapper,
                    configuration = divConfiguration,
                    lifecycleOwner = environment.lifecycleOwner
                )

                val divData = divJson.asDiv2DataWithTemplates(cardId, dependencies.errorReporter)
                Div2View(divContext).apply {
                    setData(divData, DivDataTag(cardId))
                }
            },
            modifier = modifier
        )
    }

    override fun setGlobalVariables(variables: Map<String, Any>) {
        variables.forEach { (name, value) ->
            val variable = when (value) {
                is Boolean -> Variable.BooleanVariable(name, value)
                is String -> Variable.StringVariable(name, value)
                is Long -> Variable.IntegerVariable(name, value)
                is Double -> Variable.DoubleVariable(name, value)
                is Uri -> Variable.UrlVariable(name, value)
                is Int -> Variable.ColorVariable(name, value)
                is JSONArray -> Variable.ArrayVariable(name, value)
                is JSONObject -> Variable.DictVariable(name, value)
                else -> null
            }
            variable?.let { variableController.putOrUpdate(it) }
        }
    }
}

private class ComposeActionHandler(
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
        actionHandler?.handle(uri.toString())
        return true
    }
}

private class ComposeParsingLogger(
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
    val environment = DivParsingEnvironment(ComposeParsingLogger(cardId, errorReporter))
    environment.parseTemplates(templates)
    return DivData(environment, card)
}
