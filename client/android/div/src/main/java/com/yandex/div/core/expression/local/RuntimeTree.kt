package com.yandex.div.core.expression.local

import com.yandex.div.core.expression.ExpressionsRuntime

internal class RuntimeTree {
    private val runtimesToNodes = mutableMapOf<ExpressionsRuntime, RuntimeNode>()
    private val pathToNodes = mutableMapOf<String, RuntimeNode>()

    fun storeRuntime(
        runtime: ExpressionsRuntime,
        parentRuntime: ExpressionsRuntime?,
        path: String
    ) {
        val node = RuntimeNode(runtime, path)
        pathToNodes[path] = node
        runtimesToNodes[runtime] = node
        parentRuntime?.let { runtimesToNodes[it]?.children?.add(node) }
    }

    private fun RuntimeNode.invokeRecursively(callback: (RuntimeNode) -> Unit) {
        callback.invoke(this)
        children.forEach { it.invokeRecursively(callback) }
    }

    fun invokeRecursively(
        expressionsRuntime: ExpressionsRuntime,
        path: String,
        callback: (RuntimeNode) -> Unit
    ) {
        val node = runtimesToNodes[expressionsRuntime] ?: return
        if (node.path.startsWith(path)) {
            node.invokeRecursively(callback)
            return
        }

        node.children.forEach {
            if (it.path.startsWith(path)) it.invokeRecursively(callback)
        }
    }

    fun getPathToRuntimes() = pathToNodes.map { it.key to it.value.runtime }.toMap()
    
    internal class RuntimeNode (
        val runtime: ExpressionsRuntime,
        val path: String,
        val children: MutableList<RuntimeNode> = mutableListOf(),
    )
}
