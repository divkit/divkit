package com.yandex.div.core.actions

import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivRequestExecutor
import com.yandex.div.core.util.ContainerFinder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.evaluable.MissingVariableException
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.variables.name
import com.yandex.div.internal.variables.variableValueToEvaluableValue
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionTyped
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DivActionTypedSubmitHandler @Inject constructor(
    private val requestExecutor: DivRequestExecutor,
) : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean {
        val submitAction = (action as? DivActionTyped.Submit)?.value ?: return false

        val containerId = submitAction.containerId.evaluate(resolver)
        val container = ContainerFinder(containerId).findContainer(view) ?: return false

        val headers = submitAction.request.headers?.map {
            DivRequestExecutor.Header(it.name.evaluate(resolver), it.value.evaluate(resolver))
        }

        val request = DivRequestExecutor.Request(
            submitAction.request.url.evaluate(resolver),
            submitAction.request.method.evaluate(resolver).toString(),
            headers,
            createBody(container, view),
        )

        val callback = createCallback(submitAction.onSuccessActions, submitAction.onFailActions, view, resolver)
        val loadRef = requestExecutor.execute(request, callback)
        view.addLoadReference(loadRef, view)
        return true
    }

    private fun createBody(container: DivItemBuilderResult, view: Div2View): String {
        val variables = container.div.value().variables
        if (variables.isNullOrEmpty()) return ""

        val body = JSONObject()
        variables.forEach {
            val name = it.name
            container.expressionResolver.getVariable(name)?.let {
                variable -> body.put(name, variable.getValue().variableValueToEvaluableValue())
            } ?: view.logError(MissingVariableException(name))
        }
        return body.toString()
    }

    private fun createCallback(
        onSuccessActions: List<DivAction>?,
        onFailActions: List<DivAction>?,
        view: Div2View,
        resolver: ExpressionResolver,
    ): DivRequestExecutor.Callback? {
        if (onSuccessActions.isNullOrEmpty() && onFailActions.isNullOrEmpty()) return null

        return object : DivRequestExecutor.Callback {

            override fun onSuccess() {
                onSuccessActions?.handle()
            }

            override fun onFail() {
                onFailActions?.handle()
            }

            private fun List<DivAction>.handle() {
                view.bulkActions {
                    forEach { view.handleAction(it, DivActionHandler.DivActionReason.SUBMIT, resolver) }
                }
            }
        }
    }

}
