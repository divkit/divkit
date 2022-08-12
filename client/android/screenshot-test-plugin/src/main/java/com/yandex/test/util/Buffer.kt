package com.yandex.test.util

import java.util.ArrayDeque

interface Buffer<T> : Iterable<T> {

    val capacity: Int

    val size: Int

    fun last(): T

    fun lastOrNull(): T?
}

class CircularBuffer<T>(override val capacity: Int) : Buffer<T> {

    init {
        require(capacity >= 1) { "Unable to create a circular buffer with zero capacity." }
    }

    private val buffer = ArrayDeque<T>(capacity)

    override val size: Int
        get() = buffer.size

    override fun last(): T = buffer.last

    override fun lastOrNull(): T? = buffer.peekLast()

    fun add(element: T) {
        if (size == capacity) {
            buffer.removeFirst()
        }
        buffer.addLast(element)
    }

    override fun iterator(): Iterator<T> = buffer.iterator()
}
