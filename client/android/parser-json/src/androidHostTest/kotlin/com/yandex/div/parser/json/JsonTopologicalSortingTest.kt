package com.yandex.div.parser.json

import com.yandex.div.core.json.JsonObject
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.serialization.ParsingContext
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertFailsWith

class JsonTopologicalSortingTest {

    private val errorLogger = ThrowingErrorLogger()

    private val context = mock<ParsingContext> {
        on { logger } doReturn errorLogger
    }

    @Test
    fun `sorting of empty json`() {
        val json = JsonObject()
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertTrue(sorted.isEmpty())
    }

    @Test
    fun `sorting of forward ordered inheritance`() {
        val json = read("forward-ordered-inheritance.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-2" to setOf("entity-1"),
                "entity-3" to setOf("entity-2"),
                "entity-4" to setOf("entity-3"),
            ), sorted
        )
    }

    @Test
    fun `sorting of forward ordered composition`() {
        val json = read("forward-ordered-composition.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-2" to setOf("entity-1"),
                "entity-3" to setOf("entity-2"),
                "entity-4" to setOf("entity-3"),
            ), sorted
        )
    }

    @Test
    fun `sorting of forward ordered mixed usage`() {
        val json = read("forward-ordered-mixed-usage.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-2" to setOf("entity-1"),
                "entity-3" to setOf("entity-2"),
                "entity-4" to setOf("entity-3"),
            ), sorted
        )
    }

    @Test
    fun `sorting of reverse ordered inheritance`() {
        val json = read("reverse-ordered-inheritance.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-2" to setOf("entity-1"),
                "entity-3" to setOf("entity-2"),
                "entity-4" to setOf("entity-3"),
            ), sorted
        )
    }

    @Test
    fun `sorting of reverse ordered composition`() {
        val json = read("reverse-ordered-composition.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-2" to setOf("entity-1"),
                "entity-3" to setOf("entity-2"),
                "entity-4" to setOf("entity-3"),
            ), sorted
        )
    }

    @Test
    fun `sorting of reverse ordered mixed usage`() {
        val json = read("reverse-ordered-mixed-usage.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-2" to setOf("entity-1"),
                "entity-3" to setOf("entity-2"),
                "entity-4" to setOf("entity-3"),
            ), sorted
        )
    }

    @Test
    fun `sorting of forward ordered deep composition`() {
        val json = read("forward-ordered-deep-composition.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-2" to setOf(),
                "entity-3" to setOf("entity-2", "entity-1"),
            ), sorted
        )
    }

    @Test
    fun `sorting of reverse ordered deep composition`() {
        val json = read("reverse-ordered-deep-composition.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-2" to setOf(),
                "entity-1" to setOf(),
                "entity-3" to setOf("entity-2", "entity-1"),
            ), sorted
        )
    }

    @Test
    fun `sorting of parallel dependency inheritance`() {
        val json = read("parallel-dependency-inheritance.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-2" to setOf(),
                "entity-4" to setOf("entity-2"),
                "entity-1" to setOf(),
                "entity-3" to setOf("entity-1"),
            ), sorted
        )
    }

    @Test
    fun `sorting of parallel dependency composition`() {
        val json = read("parallel-dependency-composition.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-2" to setOf(),
                "entity-4" to setOf("entity-2"),
                "entity-1" to setOf(),
                "entity-3" to setOf("entity-1"),
            ), sorted
        )
    }

    @Test
    fun `sorting of parallel dependency mixed usage`() {
        val json = read("parallel-dependency-mixed-usage.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-2" to setOf(),
                "entity-4" to setOf("entity-2"),
                "entity-1" to setOf(),
                "entity-3" to setOf("entity-1"),
            ), sorted
        )
    }

    @Test
    fun `sorting of tree dependency inheritance`() {
        val json = read("tree-dependency-inheritance.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-3" to setOf("entity-1"),
                "entity-4" to setOf("entity-3"),
                "entity-2" to setOf("entity-1"),
            ), sorted
        )
    }

    @Test
    fun `sorting of tree dependency composition`() {
        val json = read("tree-dependency-composition.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-3" to setOf("entity-1"),
                "entity-4" to setOf("entity-3"),
                "entity-2" to setOf("entity-1"),
            ), sorted
        )
    }

    @Test
    fun `sorting of tree dependency mixed usage`() {
        val json = read("tree-dependency-mixed-usage.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-3" to setOf("entity-1"),
                "entity-4" to setOf("entity-3"),
                "entity-2" to setOf("entity-1"),
            ), sorted
        )
    }

    @Test
    fun `sorting of graph dependency composition`() {
        val json = read("graph-dependency-composition.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-3" to setOf("entity-1"),
                "entity-2" to setOf("entity-1"),
                "entity-4" to setOf("entity-3", "entity-2"),
            ), sorted
        )
    }

    @Test
    fun `sorting of graph dependency mixed usage`() {
        val json = read("graph-dependency-mixed-usage.json")
        val sorted = JsonTopologicalSorting.sort(context, json)

        assertMapsAreEqualRespectingOrder(
            linkedMapOf(
                "entity-1" to setOf(),
                "entity-3" to setOf("entity-1"),
                "entity-2" to setOf("entity-1"),
                "entity-4" to setOf("entity-3", "entity-2"),
            ), sorted
        )
    }

    @Test(expected = CyclicDependencyException::class)
    fun `sorting of cyclic dependency inheritance fails`() {
        val json = read("cyclic-dependency-inheritance.json")
        JsonTopologicalSorting.sort(context, json)
    }

    @Test(expected = CyclicDependencyException::class)
    fun `sorting of cyclic dependency composition fails`() {
        val json = read("cyclic-dependency-composition.json")
        JsonTopologicalSorting.sort(context, json)
    }

    @Test(expected = CyclicDependencyException::class)
    fun `sorting of cyclic dependency mixed usage fails`() {
        val json = read("cyclic-dependency-mixed-usage.json")
        JsonTopologicalSorting.sort(context, json)
    }

    @Test(expected = CyclicDependencyException::class)
    fun `sorting of self-dependent inheritance fails`() {
        val json = read("self-dependent-inheritance.json")
        JsonTopologicalSorting.sort(context, json)
    }

    @Test(expected = CyclicDependencyException::class)
    fun `sorting of self-dependent composition fails`() {
        val json = read("self-dependent-composition.json")
        JsonTopologicalSorting.sort(context, json)
    }

    @Test
    fun `sorting of missing parent inheritance fails`() {
        whenever(context.logger).thenReturn(ParsingErrorLogger.LOG)

        val json = read("missing-parent-inheritance.json")
        assertFailsWith<ParsingException> {
            JsonTopologicalSorting.sort(context, json)
        }
    }

    @Test
    fun `sorting of empty parent inheritance fails`() {
        whenever(context.logger).thenReturn(ParsingErrorLogger.LOG)

        val json = read("missing-parent-inheritance.json")
        assertFailsWith<ParsingException> {
            JsonTopologicalSorting.sort(context, json)
        }
    }

    @Test
    fun `sorting of empty parent composition logs error`() {
        val json = read("empty-parent-composition.json")
        assertFailsWith<LoggedException> {
            JsonTopologicalSorting.sort(context, json)
        }
    }

    private fun read(filename: String): JsonObject {
        val jsonStream = javaClass.classLoader?.getResourceAsStream("com/yandex/div/parser/json/$filename")
        return if (jsonStream != null) {
            JsonObject(jsonStream.reader().readText())
        } else {
            JsonObject()
        }
    }

    private fun <K, V> assertMapsAreEqualRespectingOrder(
        expected: Map<K, V>,
        actual: Map<K, V>
    ) {
        assertTrue("Expected: $expected, actual: $actual", expected.equalsRespectingOrder(actual))
    }

    private fun <K, V> Map<K, V>.equalsRespectingOrder(other: Map<K, V>): Boolean {
        if (size != other.size) return false

        val it1 = iterator()
        val it2 = other.iterator()
        while (it1.hasNext()) {
            val entry1 = it1.next()
            val entry2 = it2.next()
            if (entry1.key != entry2.key || entry1.value != entry2.value) {
                return false
            }
        }
        return true
    }
}
