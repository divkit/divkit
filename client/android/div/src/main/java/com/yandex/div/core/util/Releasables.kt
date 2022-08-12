@file:Suppress("UNCHECKED_CAST")

package com.yandex.div.core.util

import android.view.View
import androidx.collection.SparseArrayCompat
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.expression.ExpressionSubscriber
import com.yandex.div.core.view2.Releasable

internal val View.releasableList: Iterable<Releasable>?
    get() = (getTag(R.id.div_releasable_list) as? SparseArrayCompat<Releasable>)?.toIterable()

internal val View.expressionSubscriber: ExpressionSubscriber
    get() {
        if (this is ExpressionSubscriber) {
            return this
        }

        val releasableList = getTag(R.id.div_releasable_list) as? SparseArrayCompat<Releasable>
            ?: SparseArrayCompat<Releasable>().also { setTag(R.id.div_releasable_list, it) }
        return releasableList.get(INDEX_EXPRESSION_SUBSCRIBER) as? ExpressionSubscriber
            ?: ExpressionSubscriberImpl().also { releasableList.put(INDEX_EXPRESSION_SUBSCRIBER, it) }
    }


private const val INDEX_EXPRESSION_SUBSCRIBER = 0

private class ExpressionSubscriberImpl : ExpressionSubscriber {
    override val subscriptions = mutableListOf<Disposable>()
}
