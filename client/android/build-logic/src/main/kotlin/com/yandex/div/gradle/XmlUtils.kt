package com.yandex.div.gradle

import org.w3c.dom.Element
import org.w3c.dom.Node

internal fun Node.appendElement(
    tagName: String,
    textValue: String? = null,
): Element {
    val element = ownerDocument.createElement(tagName)
    appendChild(element)

    if (textValue != null) {
        val textNode = ownerDocument.createTextNode(textValue)
        element.appendChild(textNode)
    }
    return element
}

internal fun Node.find(predicate: (Node) -> Boolean): Node? {
    children().forEach { node ->
        if (predicate(node)) {
            return node
        }
    }
    return null
}

internal fun Node.children(): Iterable<Node> = object : Iterable<Node> {
    override fun iterator() = childrenIterator()
}

private fun Node.childrenIterator(): Iterator<Node> = object : Iterator<Node> {

    private var current = firstChild

    override fun hasNext() = current != null

    override fun next(): Node {
        val next = current
        current = current!!.nextSibling
        return next
    }
}
