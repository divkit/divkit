package com.yandex.div.internal.actions

import android.net.Uri
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivActionScrollBy
import com.yandex.div2.DivActionScrollDestination
import com.yandex.div2.DivActionScrollTo
import com.yandex.div2.EndDestination
import com.yandex.div2.IndexDestination
import com.yandex.div2.ItemIdDestination
import com.yandex.div2.OffsetDestination
import com.yandex.div2.StartDestination

@InternalApi
object DivScrollActionParser {
    fun matches(authority: String?): Boolean = when (authority) {
        "set_current_item", "scroll_to_item_id", "scroll_to_start", "scroll_to_end",
        "scroll_to_position" -> true
        else -> false
    }

    fun matchesScrollBy(authority: String?): Boolean = when (authority) {
        "set_next_item", "set_previous_item", "scroll_forward", "scroll_backward" -> true
        else -> false
    }

    fun parse(uri: Uri, reportError: (String) -> Unit): DivActionScrollTo? {
        val id = uri.getQueryParameter("id") ?: run {
            reportError("id param is required to set item")
            return null
        }
        val destination = when (uri.authority) {
            "set_current_item" -> {
                val rawItem = uri.getQueryParameter("item") ?: run {
                    reportError("item is required to set current item")
                    return null
                }
                val item = rawItem.toIntOrNull() ?: run {
                    reportError("$rawItem is not a number")
                    return null
                }
                DivActionScrollDestination.Index(IndexDestination(Expression.constant(item.toLong())))
            }
            "scroll_to_item_id" -> {
                val itemId = uri.getQueryParameter("item_id") ?: run {
                    reportError("item_id is required to scroll to item with id")
                    return null
                }
                DivActionScrollDestination.ItemId(ItemIdDestination(Expression.constant(itemId)))
            }
            "scroll_to_position" -> DivActionScrollDestination.Offset(
                OffsetDestination(Expression.constant(parseStep(uri, reportError).toLong()))
            )
            "scroll_to_start" -> DivActionScrollDestination.Start(StartDestination())
            "scroll_to_end" -> DivActionScrollDestination.End(EndDestination())
            else -> return null
        }
        return DivActionScrollTo(
            id = Expression.constant(id),
            destination = destination,
            animated = Expression.constant(uri.getQueryParameter("animated")?.toBoolean() ?: true),
        )
    }

    fun parseScrollBy(uri: Uri, reportError: (String) -> Unit): DivActionScrollBy? {
        if (!matchesScrollBy(uri.authority)) return null
        val id = uri.getQueryParameter("id") ?: run {
            reportError("id param is required to set item")
            return null
        }
        val requestedStep = parseStep(uri, reportError)
        val step = when (uri.authority) {
            "set_previous_item", "scroll_backward" -> -requestedStep
            else -> requestedStep
        }
        val isItemScroll = uri.authority == "set_next_item" || uri.authority == "set_previous_item"
        val overflow = if (step == 0) {
            DivActionScrollBy.Overflow.CLAMP
        } else {
            parseOverflow(uri.getQueryParameter("overflow"), reportError)
        }
        return DivActionScrollBy(
            id = Expression.constant(id),
            itemCount = Expression.constant(if (isItemScroll) step.toLong() else 0L),
            offset = Expression.constant(if (isItemScroll) 0L else step.toLong()),
            overflow = Expression.constant(overflow),
            animated = Expression.constant(uri.getQueryParameter("animated")?.toBoolean() ?: true),
        )
    }

    fun parseStep(uri: Uri, reportError: (String) -> Unit): Int {
        val rawStep = uri.getQueryParameter("step") ?: return 1
        return rawStep.toIntOrNull() ?: run {
            reportError("$rawStep is not a number")
            1
        }
    }

    fun parseOverflow(overflow: String?, reportError: (String) -> Unit): DivActionScrollBy.Overflow {
        return when (overflow) {
            null, "clamp" -> DivActionScrollBy.Overflow.CLAMP
            "ring" -> DivActionScrollBy.Overflow.RING
            else -> {
                reportError("Unsupported overflow $overflow")
                DivActionScrollBy.Overflow.CLAMP
            }
        }
    }
}
