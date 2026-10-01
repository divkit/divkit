package com.yandex.div.core

/**
 * A container for a list of observers.
 *
 *
 * This container can be modified during iteration without invalidating the iterator.
 * So, it safely handles the case of an observer removing itself or other observers from the list
 * while observers are being notified.
 *
 *
 * The implementation (and the interface) is heavily influenced by the C++ ObserverList.
 * Notable differences:
 * - The iterator implements NOTIFY_EXISTING_ONLY.
 * - The range-based for loop is left to the clients to implement in terms of iterator().
 *
 *
 * This class is not threadsafe. Observers MUST be added, removed and will be notified on the same
 * thread this is created.
 *
 * @param E The type of observers that this list should hold.
 */
@Suppress("TooManyFunctions")
public open class ObserverList<E> : Iterable<E> {
    /**
     * Extended iterator interface that provides rewind functionality.
     */
    public interface RewindableIterator<E> : MutableIterator<E> {
        /**
         * Rewind the iterator back to the beginning.
         *
         * If we need to iterate multiple times, we can avoid iterator object reallocation by using
         * this method.
         */
        public fun rewind()
    }

    /**
     * Returns true if the ObserverList contains no observers.
     */
    public open fun isEmpty(): Boolean = count == 0

    public val isEmpty: Boolean
        @JvmName("empty") get() = isEmpty()

    private val observers: MutableList<E?> = ArrayList()

    private var iterationDepth = 0
    private var count = 0
    private var needsCompact = false

    /**
     * Add an observer to the list.
     *
     *
     * An observer should not be added to the same list more than once. If an iteration is already
     * in progress, this observer will be not be visible during that iteration.
     *
     * @return true if the observer list changed as a result of the call.
     */
    public open fun addObserver(obs: E?): Boolean {
        // Avoid adding null elements to the list as they may be removed on a compaction.
        if (obs == null || observers.contains(obs)) {
            return false
        }

        // Structurally modifying the underlying list here. This means we
        // cannot use the underlying list's iterator to iterate over the list.
        val result = observers.add(obs)
        check(result)

        ++count
        return true
    }

    /**
     * Remove an observer from the list if it is in the list.
     *
     * @return true if an element was removed as a result of this call.
     */
    public open fun removeObserver(obs: E?): Boolean {
        val index = if (obs == null) -1 else observers.indexOf(obs)
        if (index == -1) {
            return false
        }

        if (iterationDepth == 0) {
            // No one is iterating over the list.
            observers.removeAt(index)
        } else {
            needsCompact = true
            observers[index] = null
        }
        --count
        check(count >= 0)

        return true
    }

    public open fun hasObserver(obs: E?): Boolean {
        return observers.contains(obs)
    }

    public open fun clear() {
        count = 0

        if (iterationDepth == 0) {
            observers.clear()
            return
        }

        val size = observers.size
        needsCompact = needsCompact or (size != 0)
        for (i in 0..<size) {
            observers[i] = null
        }
    }

    /**
     * Returns the number of observers currently registered in the ObserverList.
     * This is equivalent to the number of non-empty spaces in [observers].
     */
    public open fun size(): Int {
        return count
    }

    /**
     * Compact the underlying list by removing null elements.
     *
     *
     * Should only be called when iterationDepth is zero.
     */
    private fun compact() {
        check(iterationDepth == 0)
        for (i in observers.indices.reversed()) {
            if (observers[i] == null) {
                observers.removeAt(i)
            }
        }
    }

    private fun incrementIterationDepth() {
        iterationDepth++
    }

    private fun decrementIterationDepthAndCompactIfNeeded() {
        iterationDepth--
        check(iterationDepth >= 0)
        if (iterationDepth > 0) return
        if (!needsCompact) return
        needsCompact = false
        compact()
    }

    /**
     * Returns the size of the underlying storage of the ObserverList.
     * It will take into account the empty spaces inside [observers].
     */
    private fun capacity(): Int {
        return observers.size
    }

    private fun getObserverAt(index: Int): E? {
        return observers[index]
    }

    override fun iterator(): MutableIterator<E> {
        return ObserverListIterator()
    }

    /**
     * It's the same as [iterator] but the return type is
     * [RewindableIterator]. Use this iterator type if you need to use
     * [RewindableIterator.rewind].
     */
    public open fun rewindableIterator(): RewindableIterator<E> {
        return ObserverListIterator()
    }

    private inner class ObserverListIterator : RewindableIterator<E> {
        private var listEndMarker: Int
        private var index = 0
        private var isExhausted = false

        init {
            incrementIterationDepth()
            listEndMarker = capacity()
        }

        override fun rewind() {
            compactListIfNeeded()
            incrementIterationDepth()
            listEndMarker = capacity()
            isExhausted = false
            index = 0
        }

        override fun hasNext(): Boolean {
            var lookupIndex = index
            while (lookupIndex < listEndMarker
                && getObserverAt(lookupIndex) == null) {
                lookupIndex++
            }
            if (lookupIndex < listEndMarker) return true

            // We have reached the end of the list, allow for compaction.
            compactListIfNeeded()
            return false
        }

        override fun next(): E {
            // Advance if the current element is null.
            while (index < listEndMarker && getObserverAt(index) == null) {
                index++
            }
            if (index < listEndMarker) return requireNotNull(getObserverAt(index++))

            // We have reached the end of the list, allow for compaction.
            compactListIfNeeded()
            throw NoSuchElementException()
        }

        override fun remove() {
            throw UnsupportedOperationException()
        }

        private fun compactListIfNeeded() {
            if (!isExhausted) {
                isExhausted = true
                decrementIterationDepthAndCompactIfNeeded()
            }
        }
    }

    /**
     * Returns [RewindableIterator] instance that iterate in reversed order.
     */
    public open fun reverseIterator(): RewindableIterator<E> {
        return ObserverListReversedIterator()
    }

    private inner class ObserverListReversedIterator : RewindableIterator<E> {
        private var index: Int
        private var isExhausted = false

        init {
            incrementIterationDepth()
            index = capacity() - 1
        }

        override fun rewind() {
            compactListIfNeeded()
            incrementIterationDepth()
            isExhausted = false
            index = capacity() - 1
        }

        override fun hasNext(): Boolean {
            var lookupIndex = index
            while (lookupIndex >= 0
                && getObserverAt(lookupIndex) == null) {
                lookupIndex--
            }
            if (lookupIndex >= 0) return true

            // We have reached the end of the list, allow for compaction.
            compactListIfNeeded()
            return false
        }

        override fun next(): E {
            // Advance if the current element is null.
            while (index >= 0 && getObserverAt(index) == null) {
                index--
            }
            if (index >= 0) return requireNotNull(getObserverAt(index--))

            // We have reached the end of the list, allow for compaction.
            compactListIfNeeded()
            throw NoSuchElementException()
        }

        override fun remove() {
            throw UnsupportedOperationException()
        }

        private fun compactListIfNeeded() {
            if (!isExhausted) {
                isExhausted = true
                decrementIterationDepthAndCompactIfNeeded()
            }
        }
    }
}
