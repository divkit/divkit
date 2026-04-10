package com.yandex.div.core.util

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.DivTreeVisitor
import com.yandex.div.internal.core.toItemBuilderResult
import com.yandex.div2.Div

internal class ContainerFinder(private val id: String) : DivTreeVisitor<Unit>() {

    private val containers = mutableListOf<DivItemBuilderResult>()

    fun findContainer(view: Div2View): DivItemBuilderResult? {
        val data = view.divData ?: return null
        visit(data, view.bindingContext)

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
            containers.add(data.toItemBuilderResult(context.expressionResolver))
        }
    }
}
