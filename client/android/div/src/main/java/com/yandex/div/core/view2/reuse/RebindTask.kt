package com.yandex.div.core.view2.reuse

import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.bindingContext
import com.yandex.div.core.view2.reuse.util.combineTokens
import com.yandex.div.internal.core.toItemBuilderResult
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivData

internal class RebindTask(
    private val div2View: Div2View,
    private val divBinder: DivBinder,
    private val oldResolver: ExpressionResolver,
    private val newResolver: ExpressionResolver,
    private val reporter: ComplexRebindReporter,
) {
    private val bindingPoints = mutableSetOf<ExistingToken>()
    private val idsToBind = mutableListOf<ExistingToken>()

    private val aloneExisting = mutableListOf<ExistingToken>()
    private val aloneNew = mutableListOf<NewToken>()
    private val aloneIds = mutableMapOf<String, ExistingToken>()

    var rebindInProgress = false
    val reusableList = ReusableTokenList()

    fun prepareAndRebind(
        oldDivData: DivData,
        newDivData: DivData,
        rootView: ViewGroup,
        path: DivStatePath,
    ): Boolean {
        clear()
        rebindInProgress = true

        val result = try {
            calculateDiff(oldDivData, newDivData, rootView)
        } catch (e: UnsupportedElementException) {
            reporter.onComplexRebindUnsupportedElementException(e)
            false
        }

        if (!result) return false

        return rebind(path)
    }

    fun clear() {
        rebindInProgress = false
        reusableList.clear()
        bindingPoints.clear()
        aloneExisting.clear()
        aloneNew.clear()
    }

    private fun calculateDiff(
        oldDivData: DivData,
        newDivData: DivData,
        rootView: ViewGroup,
    ): Boolean {
        val existingItem = div2View.stateToBind(oldDivData)?.div ?: run {
            reporter.onComplexRebindNoDivInState()
            return false
        }
        val existingToken = ExistingToken(
            item = existingItem.toItemBuilderResult(oldResolver),
            view = rootView,
            childIndex = 0,
            parentToken = null,
        )
        val newItem = div2View.stateToBind(newDivData)?.div ?: run {
            reporter.onComplexRebindNoDivInState()
            return false
        }
        val newToken = NewToken(
            item = newItem.toItemBuilderResult(newResolver),
            childIndex = 0,
            lastExistingParent = null,
        )

        if (existingToken.isCombinable(newToken)) {
            doNodeInSameMode(existingToken, newToken)
        } else {
            doNodeInExistingMode(existingToken)
            doNodeInNewMode(newToken)
        }

        aloneNew.forEach {
            val lastReal = it.lastExistingParent ?: let {
                reporter.onComplexRebindNoExistingParent()
                return false
            }

            reusableList.remove(lastReal)
            bindingPoints.add(lastReal)
        }

        return true
    }

    private fun doNodeInSameMode(
        existingToken: ExistingToken,
        newToken: NewToken,
    ) {
        val combinedToken = combineTokens(existingToken, newToken)
        newToken.lastExistingParent = combinedToken

        val aloneNewChild = newToken.getChildrenTokens().toMutableList()
        val aloneExistingChild = mutableListOf<ExistingToken>()

        existingToken.getChildrenTokens(combinedToken).forEach { existingChild ->
            val newChildWithSameHash = aloneNewChild.find { it.isCombinable(existingChild) }

            if (newChildWithSameHash != null) {
                doNodeInSameMode(existingChild, newChildWithSameHash)
                aloneNewChild.remove(newChildWithSameHash)
            } else {
                aloneExistingChild.add(existingChild)
            }
        }

        if (aloneNewChild.size != aloneExistingChild.size) {
            bindingPoints.add(combinedToken)
        } else {
            reusableList.add(combinedToken)
        }

        aloneExistingChild.forEach {
            doNodeInExistingMode(it)
        }

        aloneNewChild.forEach {
            doNodeInNewMode(it)
        }
    }

    private fun doNodeInExistingMode(token: ExistingToken) {
        val id = token.div.value().id
        if (id != null) {
            aloneIds[id] = token
        } else {
            aloneExisting.add(token)
        }

        token.getChildrenTokens().forEach {
            doNodeInExistingMode(it)
        }
    }

    private fun doNodeInNewMode(newToken: NewToken) {
        val existingWithSameHash = aloneExisting.find { it.isCombinable(newToken) }

        if (existingWithSameHash != null) {
            aloneExisting.remove(existingWithSameHash)
            doNodeInSameMode(existingWithSameHash, newToken)
        } else {
            val id = newToken.div.value().id
            val existingIdToken = if (id != null) aloneIds[id] else null

            if (id != null && existingIdToken != null
                && existingIdToken.div.javaClass == newToken.div.javaClass
                && DivComparator.areValuesReplaceable(
                    existingIdToken.div.value(),
                    newToken.div.value(),
                    oldResolver,
                    newResolver
                )
            ) {
                aloneIds.remove(id)
                val combinedToken = combineTokens(existingIdToken, newToken)
                idsToBind.add(combinedToken)
            } else {
                aloneNew.add(newToken)
            }
            newToken.getChildrenTokens().forEach {
                doNodeInNewMode(it)
            }
        }
    }

    @MainThread
    private fun rebind(path: DivStatePath): Boolean {
        if (bindingPoints.isEmpty() && reusableList.isEmpty()) {
            reporter.onComplexRebindNothingToBind()
            return false
        }

        aloneExisting.forEach {
            releaseIfNecessary(it.div, it.view)
            div2View.unbindViewFromDiv(it.view)
        }

        aloneIds.values.forEach {
            releaseIfNecessary(it.div, it.view)
            div2View.unbindViewFromDiv(it.view)
        }

        bindingPoints.forEach {
            if (bindingPoints.contains(it.parentToken)) return@forEach

            val bindingContext = it.view.bindingContext ?: div2View.bindingContext
            divBinder.bind(bindingContext, it.view, it.item.div, path)
        }

        idsToBind.forEach {
            if (bindingPoints.contains(it.parentToken)) return@forEach

            val bindingContext = it.view.bindingContext ?: div2View.bindingContext
            divBinder.bind(bindingContext, it.view, it.item.div, path)
        }

        clear()
        reporter.onComplexRebindSuccess()
        return true
    }

    private fun releaseIfNecessary(div: Div, view: View) {
        when (div) {
            is Div.Custom,
            is Div.Video -> div2View.releaseViewVisitor.visit(view)
            else -> Unit
        }
    }

    internal class UnsupportedElementException(type: Class<*>): IllegalArgumentException() {
        override val message: String = "$type is unsupported by complex rebind"
    }

    companion object {
        const val TAG = "RebindTask"
    }
}
