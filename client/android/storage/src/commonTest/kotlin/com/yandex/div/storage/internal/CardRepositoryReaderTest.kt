package com.yandex.div.storage.internal

import com.yandex.div.storage.internal.CardRepositoryReader.ReadResult
import kotlin.test.Test
import kotlin.test.assertEquals

class CardRepositoryReaderTest {
    @Test
    fun `get loads distinct misses and keeps the order of loaded cards followed by hits`() {
        val cached = Card("cached")
        val first = Card("first")
        val second = Card("second")
        var loads = 0
        val reader = reader { ids, excluded ->
            loads++
            assertEquals(listOf("second", "first"), ids.toList())
            assertEquals(emptySet(), excluded)
            ReadResult(listOf(first, second), emptyList())
        }
        reader.updateCache(listOf(cached))

        val result = reader.get(listOf("cached", "second", "cached", "first", "second"))

        assertEquals(listOf(first, second, cached, cached), result.cards)
        assertEquals(listOf(second, first), reader.get(listOf("second", "first")).cards)
        assertEquals(1, loads)
    }

    @Test
    fun `getAll combines cached and loaded cards and retries only failed IDs`() {
        val cached = Card("cached")
        val loaded = Card("loaded")
        val recovered = Card("failed")
        val failure = ReadError(recovered.id)
        val requests = mutableListOf<Pair<Set<String>, Set<String>>>()
        val reader = reader { ids, excluded ->
            requests += ids to excluded
            when (requests.size) {
                1 -> ReadResult(listOf(loaded), listOf(failure))
                2 -> ReadResult(listOf(recovered), emptyList())
                else -> error("Unexpected storage read")
            }
        }
        reader.updateCache(listOf(cached))

        assertEquals(ReadResult(listOf(loaded, cached), listOf(failure)), reader.getAll())
        assertEquals(ReadResult(listOf(recovered, cached, loaded), emptyList()), reader.getAll())
        assertEquals(ReadResult(listOf(cached, loaded, recovered), emptyList()), reader.getAll())
        assertEquals(
            listOf(emptySet<String>() to setOf("cached"), setOf("failed") to emptySet()),
            requests,
        )
    }

    @Test
    fun `update and remove change what the synchronized cache returns`() {
        val first = Card("first")
        val second = Card("second")
        val updated = first.copy(revision = 2)
        val requests = mutableListOf<Set<String>>()
        val reader = reader { ids, _ ->
            requests += ids
            ReadResult(emptyList(), emptyList())
        }
        reader.updateCache(listOf(first, second))
        assertEquals(listOf(first, second), reader.getAll().cards)

        reader.updateCache(listOf(updated))
        reader.removeFromCache(setOf(second.id))

        assertEquals(listOf(updated), reader.getAll().cards)
        assertEquals(listOf(updated), reader.get(listOf(first.id, second.id)).cards)
        assertEquals(listOf(emptySet(), setOf(second.id)), requests)
    }

    @Test
    fun `a storage result keeps a card cached while the load was in flight`() {
        val stale = Card("card")
        val updated = stale.copy(revision = 2)
        lateinit var underTest: CardRepositoryReader<Card, ReadError>
        underTest = reader { _, _ ->
            underTest.updateCache(listOf(updated))
            ReadResult(listOf(stale), emptyList())
        }

        assertEquals(listOf(updated), underTest.get(listOf(stale.id)).cards)
        assertEquals(listOf(updated), underTest.get(listOf(stale.id)).cards)
    }

    private fun reader(load: (Set<String>, Set<String>) -> ReadResult<Card, ReadError>) =
        CardRepositoryReader(Card::id, ReadError::cardId, load)

    private data class Card(val id: String, val revision: Int = 1)

    private data class ReadError(val cardId: String?)
}
