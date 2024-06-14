package com.yandex.div.core

/**
 * Aggregation of multiple [Disposable]s to be closed at one time.
 */
public class CompositeDisposable : Disposable {

    private val disposables = mutableListOf<Disposable>()
    private var closed = false

    public fun add(disposable: Disposable) {
        require(!closed) { "close() method was called" }
        if (disposable !== Disposable.NULL) {
            disposables += disposable
        }
    }

    public fun remove(disposable: Disposable) {
        require(!closed) { "close() method was called" }
        if (disposable !== Disposable.NULL) {
            disposables -= disposable
        }
    }

    override fun close() {
        disposables.forEach(Disposable::close)
        disposables.clear()
        closed = true
    }
}

public operator fun CompositeDisposable.plusAssign(disposable: Disposable): Unit = add(disposable)

public operator fun CompositeDisposable.minusAssign(disposable: Disposable): Unit = remove(disposable)
