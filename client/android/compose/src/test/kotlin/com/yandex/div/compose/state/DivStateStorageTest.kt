package com.yandex.div.compose.state

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.state.DivStatePath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivStateStorageTest {
    private val storage = DivStateStorage()
    private val rootPath = DivStatePath.fromState(0L)

    @Test
    fun `setActiveState invokes a bound variable setter`() {
        val received = mutableListOf<String>()
        storage.bindStateIdVariable(rootPath, "menu") { received.add(it) }

        storage.setActiveState(DivStatePath.parse("0/menu/main"))

        assertEquals(listOf("main"), received)
    }

    @Test
    fun `setActiveState does not touch unbound paths`() {
        val received = mutableListOf<String>()
        storage.bindStateIdVariable(rootPath, "menu") { received.add(it) }

        storage.setActiveState(DivStatePath.parse("0/footer/compact"))

        assertEquals(emptyList<String>(), received)
    }

    @Test
    fun `unbindStateIdVariable stops sync`() {
        val received = mutableListOf<String>()
        storage.bindStateIdVariable(rootPath, "menu") { received.add(it) }
        storage.unbindStateIdVariable(rootPath, "menu")

        storage.setActiveState(DivStatePath.parse("0/menu/main"))

        assertEquals(emptyList<String>(), received)
    }

    @Test
    fun `getActiveState returns the stored state id`() {
        storage.setActiveState(DivStatePath.parse("0/menu/main"))

        assertEquals("main", storage.getActiveState(rootPath, "menu"))
    }

    @Test
    fun `clear wipes states and bindings`() {
        storage.setActiveState(DivStatePath.parse("0/menu/main"))
        storage.bindStateIdVariable(rootPath, "menu") { /* */ }

        storage.clear()

        assertNull(storage.getActiveState(rootPath, "menu"))
        storage.setActiveState(DivStatePath.parse("0/menu/details"))
        assertEquals("details", storage.getActiveState(rootPath, "menu"))
    }
}
