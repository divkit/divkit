package com.yandex.div.core.actions

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.items.DivViewWithItemsController
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionScrollBy
import com.yandex.div2.DivActionScrollDestination
import com.yandex.div2.DivActionScrollTo
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedScrollHandler @Inject constructor() : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean = when(action) {
        is DivActionTyped.ScrollBy -> {
            handleAction(action.value, scopeId, view, resolver)
            true
        }
        is DivActionTyped.ScrollTo -> {
            handleAction(action.value, scopeId, view, resolver)
            true
        }
        else -> false
    }

    private fun handleAction(
        action: DivActionScrollBy,
        scopeId: String?,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val id = action.id.evaluate(resolver)
        val offset = action.offset.evaluate(resolver).toInt()
        val step = action.itemCount.evaluate(resolver).toInt()
        val overflow = DivActionScrollBy.Overflow.toString(action.overflow.evaluate(resolver))
        val animated = action.animated.evaluate(resolver)
        val duration = action.duration?.evaluate(resolver)?.toInt()

        val viewController = DivViewWithItemsController.create(id, scopeId, view, DivActionScrollBy.TYPE) ?: return
        viewController.changeCurrentItemByStep(overflow, step, animated, duration)
        viewController.scrollByOffset(overflow, offset, animated, duration, cancelPreviousScroll = step == 0)
    }

    private fun handleAction(
        action: DivActionScrollTo,
        scopeId: String?,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val id = action.id.evaluate(resolver)
        val animated = action.animated.evaluate(resolver)
        val duration = action.duration?.evaluate(resolver)?.toInt()
        val viewController = DivViewWithItemsController.create(id, scopeId, view, DivActionScrollTo.TYPE) ?: return

        when(val destination = action.destination) {
            is DivActionScrollDestination.Offset -> {
                val offset = destination.value.value.evaluate(resolver).toInt()
                viewController.scrollTo(offset, animated, duration)
            }
            is DivActionScrollDestination.Index -> {
                val index = destination.value.value.evaluate(resolver).toInt()
                viewController.setCurrentItem(index, animated, duration)
            }
            is DivActionScrollDestination.End -> {
                viewController.scrollToEnd(animated, duration)
            }
            is DivActionScrollDestination.Start -> {
                viewController.scrollToStart(animated, duration)
            }
            is DivActionScrollDestination.ItemId -> {
                val itemId = destination.value.value.evaluate(resolver)
                try {
                    viewController.scrollToItemId(itemId, animated, duration)
                } catch (e: RuntimeException) {
                    view.logError(e)
                }
            }
        }
    }
}
