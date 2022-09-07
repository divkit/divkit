package com.yandex.div.core.expression

import com.yandex.div.core.Disposable
import com.yandex.div.core.view2.Releasable

interface ExpressionSubscriber : Releasable {

    val subscriptions: MutableList<Disposable>

    fun addSubscription(subscription: Disposable) {
        if (subscription !== Disposable.NULL) {
            subscriptions += subscription
        }
    }

    fun closeAllSubscription() {
        subscriptions.forEach { subscription ->
            subscription.close()
        }
        subscriptions.clear()
    }

    override fun release() = closeAllSubscription()
}
