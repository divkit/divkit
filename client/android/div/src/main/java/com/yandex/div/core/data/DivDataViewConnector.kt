package com.yandex.div.core.data

import com.yandex.div.core.dagger.DivDataScope
import com.yandex.div.core.expression.RuntimeStoreProvider
import com.yandex.div.core.view2.Div2View
import java.util.WeakHashMap
import javax.inject.Inject

@DivDataScope
internal class DivDataViewConnector @Inject constructor(
    private val runtimeStoreProvider: RuntimeStoreProvider
) {

    private val views = WeakHashMap<Div2View, Unit>()

    fun attach(view: Div2View) {
        views[view] = Unit
        runtimeStoreProvider.store?.attachView(view)
    }

    fun detach(view: Div2View) {
        views.remove(view) ?: return
        val store = runtimeStoreProvider.store ?: return

        store.clearBindings(view)
        if (views.isEmpty()) {
            store.cleanupRuntimes(view)
        }
    }
}
