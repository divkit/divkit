package com.yandex.div.core.view2

import android.view.View
import com.yandex.div.core.Disposable
import com.yandex.div.core.util.allSightActions
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivSightAction
import java.lang.ref.WeakReference
import java.util.WeakHashMap

internal class SightActionIsEnabledObserver(
    private val onEnable: (Div2View, ExpressionResolver, View, Div, DivSightAction) -> Unit,
    private val onDisable: (Div2View, ExpressionResolver, View, Div, DivSightAction) -> Unit
) {

    private val boundedActions = WeakHashMap<View, MutableSet<DivSightAction>>()
    private val subscriptions = HashMap<DivSightAction, Subscription>()

    private val hasSubscription = WeakHashMap<View, Unit>()

    fun observe(
        view: View,
        div2View: Div2View,
        resolver: ExpressionResolver,
        div: Div,
        actions: List<DivSightAction> = div.value().allSightActions
    ) {
        view.addSubscriptionIfNeeded()
        boundedActions[view] = leftJoin(
            new = actions,
            old = boundedActions[view] ?: emptySet(),
            onDelete = { action ->
                subscriptions.remove(action)?.close()
            },
            onAdd = { action ->
                cancelObserving(action)
                subscriptions[action] = Subscription(
                   disposable = action.isEnabled.observe(resolver) { isEnabled ->
                       if (isEnabled) {
                           onEnable(div2View, resolver, view, div, action)
                       } else {
                           onDisable(div2View, resolver, view, div, action)
                       }
                   },
                   owner = view
                )
            }
        )
    }

    private fun View.addSubscriptionIfNeeded() {
        if (this !in hasSubscription && this is ExpressionSubscriber) {
            addSubscription {
                cancelObserving(boundedActions.remove(this) ?: emptySet())
            }
            hasSubscription[this] = Unit
        }
    }

    private inline fun leftJoin(
        new: List<DivSightAction>,
        old: Set<DivSightAction>,
        onDelete: (DivSightAction) -> Unit,
        onAdd: (DivSightAction) -> Unit
    ): MutableSet<DivSightAction> {
        val intersect = new.intersect(old)
        val result = intersect.toMutableSet()
        for (action in old) {
            if (action in intersect) continue
            onDelete(action)
        }
        for (action in new) {
            if (action in intersect) continue
            result.add(action)
            onAdd(action)
        }
        return result
    }

    fun cancelObserving(actions: Iterable<DivSightAction>) {
        actions.forEach(::cancelObserving)
    }

    private fun cancelObserving(action: DivSightAction) {
        val subscription = subscriptions.remove(action) ?: return
        subscription.close()
        val owner = subscription.owner.get() ?: return
        boundedActions[owner]?.remove(action)
    }

    private class Subscription(
        val disposable: Disposable,
        owner: View
    ) {
        val owner = WeakReference(owner)

        fun close() {
            disposable.close()
        }
    }
}
