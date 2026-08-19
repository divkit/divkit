package com.yandex.div.core.view2

import com.yandex.div.core.Disposable

/**
 * Stores view state associated with a particular div in a [Div2View].
 */
internal interface DivViewStateStore {

    fun get(divPath: String): DivViewState?

    fun put(divPath: String, state: DivViewState)

    fun getOrPut(divPath: String, defaultValue: () -> DivViewState): DivViewState

    fun observe(divPath: String, observer: (DivViewState) -> Unit): Disposable

    companion object {
        val EMPTY: DivViewStateStore = EmptyDivViewStateStore
    }
}

private object EmptyDivViewStateStore : DivViewStateStore {

    override fun get(divPath: String): DivViewState? = null

    override fun put(divPath: String, state: DivViewState) = Unit

    override fun getOrPut(divPath: String, defaultValue: () -> DivViewState): DivViewState = defaultValue()

    override fun observe(
        divPath: String,
        observer: (DivViewState) -> Unit,
    ): Disposable = Disposable.NULL
}
