package com.yandex.div.core.data

import com.yandex.div.core.dagger.DivDataScope
import com.yandex.div.core.expression.RuntimeStoreProvider
import com.yandex.div.core.view2.Div2View
import java.util.WeakHashMap
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.withLock

@DivDataScope
internal class DivDataViewConnector(
    private val runtimeStoreProvider: RuntimeStoreProvider,
    private val lock: ReentrantLock,
) {

    @Inject
    constructor(runtimeStoreProvider: RuntimeStoreProvider) : this(runtimeStoreProvider, ReentrantLock())

    private val views = WeakHashMap<Div2View, Unit>()

    fun attach(view: Div2View) {
        lock.withLock {
            views[view] = Unit
            runtimeStoreProvider.store?.attachView(view)
        }
    }

    fun detach(view: Div2View) {
        lock.withLock {
            views.remove(view) ?: return
            val store = runtimeStoreProvider.store ?: return

            store.clearBindings(view)
            if (views.isEmpty()) {
                store.cleanupRuntimes(view)
            }
        }
    }
}
