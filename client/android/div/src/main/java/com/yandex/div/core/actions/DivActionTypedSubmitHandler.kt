package com.yandex.div.core.actions

import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivRequestExecutor
import com.yandex.div.core.expression.name
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.evaluable.MissingVariableException
import com.yandex.div.internal.core.DivTreeVisitor
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
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
        val (containerDiv, context, path) = ContainerFinder(containerId).findContainer(view) ?: return false

        val headers = submitAction.request.headers?.map {
            DivRequestExecutor.Header(it.name.evaluate(resolver), it.value.evaluate(resolver))
        }

        val request = DivRequestExecutor.Request(
            submitAction.request.url.evaluate(resolver),
            submitAction.request.method.evaluate(resolver).toString(),
            headers,
            createBody(containerDiv, context, path, view),
        )

        val callback = createCallback(submitAction.onSuccessActions, submitAction.onFailActions, view, resolver)
        val loadRef = requestExecutor.execute(request, callback)
        view.addLoadReference(loadRef, view)
        return true
    }

    private fun createBody(div: Div, context: BindingContext, path: DivStatePath, view: Div2View): String {
        val variables = div.value().variables
        if (variables.isNullOrEmpty()) return ""

        val variableController = context.runtimeStore
            ?.getOrCreateRuntime(path.fullPath, div, context.expressionResolver)?.variableController ?: return ""

        val body = JSONObject()
        variables.forEach {
            val name = it.name
            variableController.get(name)?.let { value -> body.put(name, value) }
                ?: view.logError(MissingVariableException(name))
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

    private class ContainerFinder(private val id: String) : DivTreeVisitor<Unit>() {

        private val containers = mutableListOf<Triple<Div, BindingContext, DivStatePath>>()

        fun findContainer(view: Div2View): Triple<Div, BindingContext, DivStatePath>? {
            val data = view.divData ?: return null
            data.states.forEach { state ->
                visit(state.div, view.bindingContext, DivStatePath.fromState(state))
            }

            if (containers.isEmpty()) {
                view.logError(
                    RuntimeException("Error resolving container. Elements that respond to id '$id' are not found.")
                )
                return null
            }

            if (containers.size > 1) {
                view.logError(
                    RuntimeException("Error resolving container. Found multiple elements that respond to id '$id'.")
                )
                return null
            }

            return containers.first()
        }

        override fun defaultVisit(data: Div, context: BindingContext, path: DivStatePath) {
            if (data.value().id == id) {
                containers.add(Triple(data, context, path))
            }
        }
    }
}
