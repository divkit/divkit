package com.yandex.div.core.view2.reuse

import android.view.View
import android.view.ViewGroup
import com.yandex.div.data.Hashable
import java.util.LinkedList

internal class ReusableTokenList {
    private val reusable: HashMap<Int, LinkedList<ExistingToken>> = hashMapOf()

    private val viewIndexShift: MutableMap<Int, Int> = mutableMapOf()

    fun count(): Int = reusable.size

    fun isEmpty(): Boolean = reusable.isEmpty()

    fun asList() = reusable.values.flatten()

    fun contains(hash: Int): Boolean = reusable.containsKey(hash)

    fun contains(item: Hashable): Boolean = reusable.containsKey(item.propertiesHash())

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

    fun pop(item: Hashable): ExistingToken? = pop(item.propertiesHash())

    fun get(item: Hashable): ExistingToken? = reusable[item.propertiesHash()]?.firstOrNull()

    fun getUniqueView(item: Hashable): View? {
        val hash = item.propertiesHash()
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
