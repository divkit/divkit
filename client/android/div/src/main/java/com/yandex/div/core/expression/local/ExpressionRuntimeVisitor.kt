package com.yandex.div.core.expression.local

import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.util.toVariables

internal class ExpressionRuntimeVisitor(
    private val runtimeStore: RuntimeStore,
) {
    fun visit(node: DivRuntimeTree.Node): ExpressionsRuntime {
        return runtimeStore.getOrCreateRuntime(
            path = node.path.fullPath,
            parentPath = node.path.parentFullPath,
            variables = node.div.value().variables?.toVariables(),
            triggers = node.div.value().variableTriggers,
            parentRuntime = node.parentRuntime,
        )?.also { parentRuntime ->
            node.children.forEach { childNode ->
                childNode.parentRuntime = parentRuntime
                visit(childNode)
            }
        } ?: throw AssertionError(
            "ExpressionRuntimeVisitor cannot create runtime for path = ${node.path.fullPath}"
        )
    }
}
