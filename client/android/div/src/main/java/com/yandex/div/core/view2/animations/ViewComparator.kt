package com.yandex.div.core.view2.animations

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

internal object ViewComparator {
    fun View.structureEquals(other: View): Boolean {
        if (this::class.java == other::class.java) {
            if (this is ViewGroup && other is ViewGroup) {
                return if (this.childCount == other.childCount) {
                    this.children
                        .zip(other.children)
                        .map {
                            it.first.structureEquals(it.second)
                        }
                        .reduceOrNull { a, b ->
                            a && b
                        } ?: true
                } else {
                    false
                }
            }
            return true
        }
        return false
    }
}
