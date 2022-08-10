package com.yandex.div.view.pooling

import com.yandex.div.util.removeFirstIf
import java.util.AbstractQueue
import java.util.Queue
import java.util.Spliterator
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

internal class BatchBlockingQueue<E>(backingQueue: Queue<E>) : AbstractQueue<E>(), BlockingQueue<E> {

    private val queue = backingQueue
    private val lock = ReentrantLock()
    private val notEmpty = lock.newCondition()

    override val size: Int
        get() = locked {
            queue.size
        }

    override fun add(element: E): Boolean = offer(element)

    override fun offer(element: E): Boolean {
        locked {
            queue.offer(element)
            notEmpty.signal()
        }
        return true
    }

    override fun put(element: E) {
        offer(element)
    }

    override fun offer(element: E, timeout: Long, unit: TimeUnit) = offer(element)

    override fun poll(): E? {
        locked {
            return queue.poll()
        }
    }

    @Throws(InterruptedException::class)
    override fun poll(timeout: Long, unit: TimeUnit): E? {
        val result: E? = lockedInterruptibly {
            var nanos = unit.toNanos(timeout)
            while (queue.isEmpty() && nanos > 0) {
                nanos = notEmpty.awaitNanos(nanos)
            }
            queue.poll()
        }

        return result
    }

    @Throws(InterruptedException::class)
    override fun take(): E {
        val result = lockedInterruptibly {
            while (queue.isEmpty()) {
                notEmpty.await()
            }
            queue.poll()
        }

        return result
    }

    override fun peek(): E {
        locked {
            return queue.peek()
        }
    }

    override fun remove(element: E): Boolean {
        locked {
            return queue.remove(element)
        }
    }

    override fun remainingCapacity() = Int.MAX_VALUE

    inline fun removeFirstIf(predicate: (E) -> Boolean): Boolean {
        locked {
            return queue.removeFirstIf(predicate)
        }
    }

    inline fun batch(batch: BatchBlockingQueue<E>.() -> Unit) {
        locked {
            batch(this)
        }
    }

    override fun drainTo(c: MutableCollection<in E>?): Int { notSupported() }

    override fun drainTo(c: MutableCollection<in E>?, maxElements: Int): Int { notSupported() }

    override fun iterator(): MutableIterator<E> { notSupported() }

    override fun spliterator(): Spliterator<E> { notSupported() }

    private fun notSupported(): Nothing = throw UnsupportedOperationException()

    private inline fun <R> locked(action: () -> R): R {
        lock.lock()
        try {
            return action()
        } finally {
            lock.unlock()
        }
    }

    private inline fun <R> lockedInterruptibly(action: () -> R): R {
        lock.lockInterruptibly()
        try {
            return action()
        } finally {
            lock.unlock()
        }
    }
}
