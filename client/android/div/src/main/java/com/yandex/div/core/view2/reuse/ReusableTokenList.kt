package com.yandex.div.core.view2.reuse

import android.view.View
import android.view.ViewGroup
import com.yandex.div2.Div
import java.util.LinkedList

internal class ReusableTokenList {
    private val reusable: HashMap<Int, LinkedList<ExistingToken>> = hashMapOf()

    private val viewIndexShift: MutableMap<Int, Int> = mutableMapOf()

    fun count(): Int = reusable.size

    fun isEmpty(): Boolean = reusable.isEmpty()

    fun asList() = reusable.values.flatten()

    fun contains(hash: Int): Boolean = reusable.containsKey(hash)

    fun contains(div: Div): Boolean = reusable.containsKey(div.propertiesHash())

    fun add(token: ExistingToken) {
        val hash = token.divHash

        reusable.getOrPut(hash) { LinkedList<ExistingToken>() }.add(token)
    }

    fun pop(hash: Int): ExistingToken? {
        val tokens = reusable[hash]

        if (tokens.isNullOrEmpty()) return null

        return tokens.pop().also {
            if (reusable[hash].isNullOrEmpty()) {
                reusable.remove(hash)
            }
        }
    }

    fun pop(div: Div): ExistingToken? = pop(div.propertiesHash())

    fun get(div: Div): ExistingToken? = reusable[div.propertiesHash()]?.firstOrNull()

    fun getUniqueViewForDiv(div: Div): View? {
        val hash = div.propertiesHash()
        val index = viewIndexShift.getOrPut(hash) { 0 }

        return reusable[hash]?.getOrNull(index)?.let {
            viewIndexShift[hash] = index + 1
            (it.view.parent as? ViewGroup)?.removeView(it.view)

            it.view
        }
    }

    fun remove(token: ExistingToken): Boolean {
        val list = this.reusable[token.divHash] ?: return false

        return list.remove(list.find { it.view == token.view })
    }

    fun clear() {
        reusable.clear()
        viewIndexShift.clear()
    }
}
