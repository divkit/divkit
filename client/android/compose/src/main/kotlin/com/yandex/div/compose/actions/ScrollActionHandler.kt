package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.scroll.ScrollTarget
import com.yandex.div.compose.scroll.ScrollTargetStorage
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div2.DivActionScrollBy
import com.yandex.div2.DivActionScrollDestination
import com.yandex.div2.DivActionScrollTo
import javax.inject.Inject

@DivViewScope
internal class ScrollActionHandler @Inject constructor(
    private val animationsEnabledProvider: DivAnimationsEnabledProvider,
    private val reporter: DivReporter,
    private val targetStorage: ScrollTargetStorage,
) {
    fun handle(context: DivActionHandlingContext, action: DivActionScrollTo) {
        val resolver = context.expressionResolver
        val id = action.id.evaluate(resolver)
        val duration = action.duration?.evaluate(resolver)?.toInt()
        val animated = action.animated.evaluate(resolver) &&
            animationsEnabledProvider.isAnimationsEnabled() && duration != 0
        when (val destination = action.destination) {
            is DivActionScrollDestination.Index -> {
                val index = destination.value.value.evaluate(resolver)
                withTarget(id) { target ->
                    if (index in 0L until target.items.size.toLong()) {
                        target.scrollTo(index.toInt(), animated, false, duration)
                    } else {
                        reporter.reportError("Scroll index $index is not in range [0, ${target.items.size}) for '$id'")
                    }
                }
            }
            is DivActionScrollDestination.ItemId -> {
                val itemId = destination.value.value.evaluate(resolver)
                withTarget(id) { target ->
                    val indices = target.items.indices.filter { target.items[it].value().id == itemId }
                    when (indices.size) {
                        0 -> reporter.reportError("No item with id '$itemId' found in '$id' for scroll_to action")
                        1 -> target.scrollTo(indices.single(), animated, false, duration)
                        else -> reporter.reportError(
                            "Several items with id '$itemId' found in '$id' for scroll_to action"
                        )
                    }
                }
            }
            is DivActionScrollDestination.Start -> withTarget(id) { target ->
                if (target.items.isNotEmpty()) target.scrollTo(0, animated, false, duration)
            }
            is DivActionScrollDestination.End -> withTarget(id) { target ->
                if (target.items.isNotEmpty()) target.scrollTo(target.items.lastIndex, animated, true, duration)
            }
            is DivActionScrollDestination.Offset -> {
                val offset = destination.value.value.evaluate(resolver).toInt()
                withTarget(id) { target -> target.scrollToOffset(offset, animated, duration) }
            }
        }
    }

    fun handle(context: DivActionHandlingContext, action: DivActionScrollBy) {
        val resolver = context.expressionResolver
        val id = action.id.evaluate(resolver)
        val step = action.itemCount.evaluate(resolver).toInt()
        val offset = action.offset.evaluate(resolver).toInt()
        val overflow = action.overflow.evaluate(resolver)
        val duration = action.duration?.evaluate(resolver)?.toInt()
        val animated = action.animated.evaluate(resolver) &&
            animationsEnabledProvider.isAnimationsEnabled() && duration != 0
        withTarget(id, DivActionScrollBy.TYPE) { target ->
            if (step != 0 || offset != 0) target.scrollBy(step, offset, overflow, animated, duration)
        }
    }

    private fun withTarget(
        id: String,
        actionType: String = DivActionScrollTo.TYPE,
        action: (ScrollTarget) -> Unit,
    ) {
        val matches = targetStorage.get(id)
        if (matches.isEmpty()) {
            reporter.reportError("No scrollable element with id '$id' found for $actionType action")
            return
        }
        if (matches.size > 1) {
            reporter.reportError("Several scrollable elements with id '$id' found for $actionType action")
            return
        }
        action(matches.single())
    }
}
