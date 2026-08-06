package com.yandex.div.core.util

import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.DivTreeVisitor

internal class ContainerFinder(private val id: String) : DivTreeVisitor<Unit>() {

    private val containers = mutableListOf<DivBlock>()

    fun findContainer(view: Div2View): DivBlock? {
        val data = view.divData ?: return null
        visit(data, view.expressionResolver)

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

    override fun defaultVisit(divBlock: DivBlock) {
        if (divBlock.div.value().id == id) {
            containers.add(divBlock)
        }
    }
}
