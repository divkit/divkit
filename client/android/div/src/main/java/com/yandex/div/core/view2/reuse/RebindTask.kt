package com.yandex.div.core.view2.reuse

import android.view.ViewGroup
import androidx.annotation.MainThread
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.bindingContext
import com.yandex.div.core.view2.reuse.util.combineTokens
import com.yandex.div.core.view2.reuse.util.logRebindDiff
import com.yandex.div.internal.Log
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData

internal class RebindTask(
    private val div2View: Div2View,
    private val divBinder: DivBinder,
    private val resolver: ExpressionResolver,
    private val newResolver: ExpressionResolver,
    private val reporter: ComplexRebindReporter,
) {
    private val bindingPoints: MutableSet<ExistingToken> = mutableSetOf()
    private val aloneExisting = mutableListOf<ExistingToken>()
    private val aloneNew = mutableListOf<NewToken>()

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

        if (!calculateDiff(oldDivData, newDivData, rootView)) {
            return false
        }
        if (Log.isEnabled()) {
            logRebindDiff(reusableList, bindingPoints, aloneExisting, aloneNew)
        }
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
            item = DivItemBuilderResult(existingItem, resolver),
            view = rootView,
            childIndex = 0,
            parentToken = null,
        )
        val newItem = div2View.stateToBind(newDivData)?.div ?: run {
            reporter.onComplexRebindNoDivInState()
            return false
        }
        val newToken = NewToken(
            item = DivItemBuilderResult(newItem, newResolver),
            childIndex = 0,
            parentToken = null,
            lastExistingParent = null,
        )

        if (existingToken.divHash == newToken.divHash) {
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

        existingToken.getChildrenTokens().forEach { existingChild ->
            val newChildWithSameHash = aloneNewChild.find { it.divHash == existingChild.divHash }

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
        aloneExisting.add(token)
        token.getChildrenTokens().forEach {
            doNodeInExistingMode(it)
        }
    }

    private fun doNodeInNewMode(token: NewToken) {
        val existingWithSameHash = aloneExisting.find { it.divHash == token.divHash }

        if (existingWithSameHash != null) {
            aloneExisting.remove(existingWithSameHash)
            doNodeInSameMode(existingWithSameHash, token)
        } else {
            aloneNew.add(token)
            token.getChildrenTokens().forEach {
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
            div2View.releaseViewVisitor.visit(it.view)
            div2View.unbindViewFromDiv(it.view)
        }

        bindingPoints.forEach {
            if (bindingPoints.contains(it.parentToken)) return@forEach

            val bindingContext = it.view.bindingContext ?: div2View.bindingContext
            divBinder.bind(bindingContext, it.view, it.item.div, path)
        }

        clear()
        reporter.onComplexRebindSuccess()
        return true
    }

    companion object {
        const val TAG = "RebindTask"
    }
}
