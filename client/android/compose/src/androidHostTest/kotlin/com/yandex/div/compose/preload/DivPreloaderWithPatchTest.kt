package com.yandex.div.compose.preload

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.test.data.change
import com.yandex.div.test.data.container
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div2.DivPatch
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivPreloaderWithPatchTest {
    private val preloadedTypes = mutableListOf<String>()
    private var patch: DivPatch? = null
    private var failedType: String? = null

    private val data = data(
        container(
            id = "root",
            items = listOf(
                custom(type = "before"),
                custom(type = "remaining")
            )
        )
    )

    private val factory: DivCustomViewFactory = object : DivCustomViewFactory {
        @Composable
        override fun Content(modifier: Modifier, environment: DivCustomEnvironment) = Unit

        override suspend fun preloadWithResult(environment: DivCustomEnvironment): PreloadResult {
            val type = environment.data.customType
            preloadedTypes.add(type)
            if (type == "before") {
                patch?.let {
                    divContext.getViewContext(data).component.patchCoordinator.applyPatch(it)
                }
            }
            return PreloadResult(type != failedType)
        }
    }

    private val divContext = DivContext(
        baseContext = getApplicationContext(),
        configuration = DivConfiguration(
            reporter = TestReporter(),
            customViewFactories = listOf("before", "remaining", "after").associateWith { factory }
        )
    )

    @Test
    fun `preload finishes the initial layout when a patch is applied during traversal`() = runTest {
        patch = replaceRoot("after")

        val result = divContext.preload(data)

        assertTrue(result.isSuccessful)
        assertEquals(listOf("before", "remaining"), preloadedTypes)
    }

    @Test
    fun `preload reports a resource failure in the initial layout after a patch`() = runTest {
        patch = replaceRoot("after")
        failedType = "remaining"

        val result = divContext.preload(data)

        assertFalse(result.isSuccessful)
        assertEquals(listOf("before", "remaining"), preloadedTypes)
    }

    @Test
    fun `preload uses a patch applied before the call`() = runTest {
        divContext.getViewContext(data).component.patchCoordinator.applyPatch(replaceRoot("after"))

        val result = divContext.preload(data)

        assertTrue(result.isSuccessful)
        assertEquals(listOf("after"), preloadedTypes)
    }

    private fun replaceRoot(type: String): DivPatch {
        return DivPatch(
            changes = listOf(
                change(
                    id = "root",
                    items = listOf(container(id = "root", items = listOf(custom(type = type))))
                )
            )
        )
    }
}
