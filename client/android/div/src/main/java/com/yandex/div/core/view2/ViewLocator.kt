package com.yandex.div.core.view2

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.yandex.div.core.actions.logWarning
import com.yandex.div.internal.Assert

internal object ViewLocator {

    @JvmStatic
    fun findSingleViewWithTag(view: Div2View, tag: String): View? = view.findSingleViewWithTag<View>(tag).getOrNull()

    inline fun <reified T> findSingleViewWithTag(view: Div2View, tag: String, scopeId: String?): Result<T> =
        findSingleViewWithTag(view, tag, scopeId) { id, inScope -> findSingleViewWithTag(id, inScope = inScope) }

    inline fun <reified T> findSingleViewWithTag(
        view: Div2View,
        tag: String,
        scopeId: String?,
        findTargetView: View.(id: String, inScope: Boolean) -> Result<T>,
    ): Result<T> {
        scopeId ?: return view.findTargetView(tag, false)

        val scopeError = view.findSingleViewWithTag<View>(scopeId, isScope = true)
            .onSuccess { return it.findTargetView(tag, true) }
            .exceptionOrNull()

        if (scopeError !is MissingTarget) {
            val error = scopeError ?: run {
                Assert.fail("ScopeError is null")
                DuplicateTarget(scopeId, isScope = true, inScope = false)
            }
            return Result.failure(error)
        }

        return view.findTargetView(tag, false)
            .onSuccess { view.logWarning(scopeError) }
            .onFailure { if (it is DuplicateTarget) return Result.failure(scopeError) }
    }

    inline fun <reified T> View.findSingleViewWithTag(
        tag: String,
        isScope: Boolean = false,
        inScope: Boolean = false,
    ): Result<T> {
        val foundViews = findViewsWithTag<T>(tag)
        return when {
            foundViews.isEmpty() -> Result.failure(MissingTarget(tag, isScope, inScope))
            foundViews.size > 1 -> Result.failure(DuplicateTarget(tag, isScope, inScope))
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

    class MissingTarget(tag: String, isScope: Boolean, inScope: Boolean)
        : Exception(buildMessage(tag, isScope, "not found", inScope))
    class DuplicateTarget(tag: String, isScope: Boolean, inScope: Boolean)
        : Exception(buildMessage(tag, isScope, "is ambiguous", inScope))

    private fun buildMessage(tag: String, isScope: Boolean, error: String, inScope: Boolean) =
        (if (isScope) "Scope" else "Element") + " with id '$tag' $error" + (if (inScope) " in scope" else "")
}
