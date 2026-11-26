package com.yandex.div.storage.internal

import com.yandex.div.core.annotations.InternalApi
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.concurrent.Volatile

@InternalApi
public class CardRepositoryReader<Card : Any, Error : Any>(
    private val cardId: (Card) -> String,
    private val errorCardId: (Error) -> String?,
    private val loadFromStorage: (ids: Set<String>, idsToExclude: Set<String>) -> ReadResult<Card, Error>,
) {
    private val cacheLock = SynchronizedObject()
    private val cachedCards = linkedMapOf<String, Card>()

    // Null means a full read has not completed. Publish that state together with the retry IDs.
    @Volatile
    private var failedCardIds: Set<String>? = null

    public fun get(ids: List<String>): ReadResult<Card, Error> {
        if (ids.isEmpty()) return ReadResult(emptyList(), emptyList())

        val idsToLoad = ids.toMutableSet()
        val inMemoryResults = ArrayList<Card>(ids.size)
        ids.forEach { id ->
            synchronized(cacheLock) { cachedCards[id] }?.let { card ->
                inMemoryResults.add(card)
                idsToLoad.remove(id)
            }
        }

        if (idsToLoad.isEmpty()) return ReadResult(inMemoryResults, emptyList())

        val result = loadFromStorage(idsToLoad, emptySet())
        return ReadResult(cacheLoadedCards(result.cards) + inMemoryResults, result.errors)
    }

    public fun getAll(): ReadResult<Card, Error> {
        val retryIds = failedCardIds
        if (retryIds != null && retryIds.isEmpty()) return ReadResult(inMemoryValues(), emptyList())

        val idsToExclude = if (retryIds == null) {
            synchronized(cacheLock) { cachedCards.keys.toSet() }
        } else {
            emptySet()
        }
        val result = loadFromStorage(retryIds.orEmpty(), idsToExclude)
        val loaded = cacheLoadedCards(result.cards)
        val loadedIds = loaded.mapTo(HashSet(), cardId)
        val cards = loaded + inMemoryValues().filterNot { cardId(it) in loadedIds }
        failedCardIds = result.errors.mapNotNullTo(LinkedHashSet(), errorCardId)
        return ReadResult(cards, result.errors)
    }

    public fun updateCache(cards: List<Card>) {
        cards.forEach { card ->
            val id = cardId(card)
            synchronized(cacheLock) { cachedCards[id] = card }
        }
    }

    public fun removeFromCache(ids: Set<String>) {
        ids.forEach { id ->
            synchronized(cacheLock) { cachedCards.remove(id) }
        }
    }

    private fun inMemoryValues(): List<Card> = synchronized(cacheLock) { cachedCards.values.toList() }

    private fun cacheLoadedCards(cards: List<Card>): List<Card> = synchronized(cacheLock) {
        cards.map { card -> cachedCards.getOrPut(cardId(card)) { card } }
    }

    public data class ReadResult<Card, Error>(
        public val cards: List<Card>,
        public val errors: List<Error>,
    )
}
