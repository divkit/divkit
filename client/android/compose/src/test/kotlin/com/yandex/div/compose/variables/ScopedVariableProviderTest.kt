package com.yandex.div.compose.variables

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.collection.ItemVariableProvider
import com.yandex.div.compose.rootVariableProvider
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ScopedVariableProviderTest {
    private val ancestor = DivVariableController()
    private val controller = DivVariableController(ancestor)
    private val parent = ItemVariableProvider(
        rootVariableProvider(ancestor),
        mapOf("value" to 2L, "other" to 3L),
    )
    private val provider = ScopedVariableProvider(controller, parent)

    @Test
    fun `undeclared variable reads the parent even when an ancestor declares the same name`() {
        ancestor.declare(Variable.IntegerVariable("value", 7L))

        assertEquals(2L, provider.get("value"))
        assertTrue(provider.isConstant("value"))
    }

    @Test
    fun `declared variable reads the controller and no longer belongs to the parent`() {
        provider.declare(Variable.IntegerVariable("value", 7L))

        assertEquals(7L, provider.get("value"))
        assertFalse(provider.isConstant("value"))
    }

    @Test
    fun `shadowed parent updates do not notify a local subscription`() {
        provider.declare(Variable.IntegerVariable("value", 7L))
        val updates = mutableListOf<Any?>()
        provider.subscribe(listOf("value")) { updates.add(provider.get("value")) }

        parent.update(mapOf("value" to 4L, "other" to 3L))
        parent.notifyChanges()

        assertEquals(emptyList(), updates)
    }

    @Test
    fun `unshadowed parent updates notify the inherited subscription`() {
        provider.declare(Variable.IntegerVariable("value", 7L))
        val updates = mutableListOf<Any?>()
        provider.subscribe(listOf("value", "other")) { updates.add(provider.get("other")) }

        parent.update(mapOf("value" to 2L, "other" to 5L))
        parent.notifyChanges()

        assertEquals(listOf<Any?>(5L), updates)
    }

    // The only test of closing through a scope: wrapping the inherited subscription in an
    // AutoCloseable that does not close it leaves the item observer registered.
    @Test
    fun `closed inherited subscription ignores later parent updates`() {
        val updates = mutableListOf<Any?>()
        val subscription = checkNotNull(
            provider.subscribe(listOf("other")) { updates.add(provider.get("other")) }
        )
        subscription.close()

        parent.update(mapOf("value" to 2L, "other" to 5L))
        parent.notifyChanges()

        assertEquals(emptyList(), updates)
    }
}
