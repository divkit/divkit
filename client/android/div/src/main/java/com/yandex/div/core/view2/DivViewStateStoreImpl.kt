package com.yandex.div.core.view2

import com.yandex.div.core.Disposable
import com.yandex.div.core.dagger.DivDataScope
import java.util.Collections
import javax.inject.Inject

@DivDataScope
internal class DivViewStateStoreImpl @Inject constructor() : DivViewStateStore {

    private val states = Collections.synchronizedMap(mutableMapOf<String, DivViewState>())
    private val observers = mutableMapOf<String, MutableList<(DivViewState) -> Unit>>()

    override fun get(divPath: String): DivViewState? = synchronized(states) {
        states[divPath]
    }

    override fun put(divPath: String, state: DivViewState) {
        val callbacks = synchronized(states) {
            states[divPath] = state
            observers[divPath]?.toList().orEmpty()
        }
        callbacks.forEach { it(state) }
    }

    override fun getOrPut(divPath: String, defaultValue: () -> DivViewState): DivViewState =
        synchronized(states) {
            states.getOrPut(divPath, defaultValue)
        }

    override fun observe(divPath: String, observer: (DivViewState) -> Unit): Disposable {
        synchronized(states) {
            observers.getOrPut(divPath, ::mutableListOf).add(observer)
        }
        return Disposable {
            synchronized(states) {
                observers[divPath]?.let {
                    it.remove(observer)
                    if (it.isEmpty()) observers.remove(divPath)
                }
            }
        }
    }

    fun reset() = synchronized(states) {
        states.clear()
    }
}
