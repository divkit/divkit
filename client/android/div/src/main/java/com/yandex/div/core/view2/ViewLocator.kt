package com.yandex.div.core.view2

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.yandex.div.core.actions.logWarning
import com.yandex.div.internal.Assert

internal object ViewLocator {

    @JvmStatic
    fun findSingleViewWithTag(view: Div2View, tag: String): View? = view.findSingleViewWithTag<View>(tag).getOrNull()

    inline fun <reified T> findSingleViewWithTag(view: Div2View, tag: String, scopeId: String?): Result<T> {
        scopeId ?: return view.findSingleViewWithTag<T>(tag)

        val scopeError = view.findSingleViewWithTag<View>(scopeId, isScope = true)
            .onSuccess { return it.findSingleViewWithTag<T>(tag) }
            .exceptionOrNull()

        if (scopeError !is MissingTarget) {
            val error = scopeError ?: run {
                Assert.fail("ScopeError is null")
                DuplicateTarget(scopeId, isScope = true)
            }
            return Result.failure(error)
        }

        return view.findSingleViewWithTag<T>(tag)
            .onSuccess { view.logWarning(scopeError) }
            .onFailure { if (it is DuplicateTarget) return Result.failure(scopeError) }
    }

    private inline fun <reified T> View.findSingleViewWithTag(tag: String, isScope: Boolean = false): Result<T> {
        val foundViews = findViewsWithTag<T>(tag)
        return when {
            foundViews.isEmpty() -> Result.failure(MissingTarget(tag, isScope))
            foundViews.size > 1 -> Result.failure(DuplicateTarget(tag, isScope))
            else -> Result.success(foundViews.first())
        }
    }

    private inline fun <reified T> View.findViewsWithTag(tag: String): List<T> {
        val result = mutableListOf<View>()
        result.fillWithViewsWithTagTraversal(this, tag)
        return result.filterIsInstance<T>()
    }

    private fun MutableList<View>.fillWithViewsWithTagTraversal(view: View, tag: Any) {
        if (tag == view.tag) {
            add(view)
        }

        if (view !is ViewGroup) return

        view.children.forEach {
            fillWithViewsWithTagTraversal(it, tag)
        }
    }

    class MissingTarget(tag: String, isScope: Boolean) : Exception("${isScope.toMessage} with id '$tag' is missing")
    class DuplicateTarget(tag: String, isScope: Boolean) : Exception("${isScope.toMessage} with id '$tag' is ambiguous")

    private val Boolean.toMessage get() = if (this) "Scope" else "Element"
}
