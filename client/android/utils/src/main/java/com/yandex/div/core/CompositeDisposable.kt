package com.yandex.div.core

/**
 * Aggregation of multiple [Disposable]s to be closed at one time.
 */
class CompositeDisposable : Disposable {

    private val disposables = mutableListOf<Disposable>()
    private var closed = false

    fun add(disposable: Disposable) {
        require(!closed) { "close() method was called" }
        if (disposable !== Disposable.NULL) {
            disposables += disposable
        }
    }

    fun remove(disposable: Disposable) {
        require(!closed) { "close() method was called" }
        disposables -= disposable
    }

    override fun close() {
        disposables.forEach(Disposable::close)
        disposables.clear()
        closed = true
    }
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) = add(disposable)

operator fun CompositeDisposable.minusAssign(disposable: Disposable) = remove(disposable)
