package com.yandex.div.compose.layoutprovider

import androidx.compose.runtime.BroadcastFrameClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.createExpressionResolver
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import kotlinx.coroutines.test.TestScope
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class LayoutProviderStorageTest {
    private val reporter = TestReporter()
    private val storage = LayoutProviderStorage(reporter)
    private val widthVariable = Variable.IntegerVariable("width", 0)
    private val frameClock = BroadcastFrameClock()
    private val testScope = TestScope(frameClock)

    private val variableController = DivVariableController().apply {
        declare(widthVariable)
    }

    private val expressionResolver = createExpressionResolver(
        reporter = reporter,
        variableController = variableController,
    )

    private val width = rememberVariableHandler("width")

    private var frameTimeNanos = 0L

    @Test
    fun `getVariableHandler() reports an error if variable is missing`() {
        reporter.failOnError = false

        val result = storage.getVariableHandler("missing", expressionResolver)

        assertNull(result)
        assertEquals(listOf("Variable 'missing' is missing."), reporter.errors)
    }

    @Test
    fun `getVariableHandler() reports an error if variable is not an integer variable`() {
        reporter.failOnError = false

        variableController.declare(Variable.StringVariable("text", ""))

        val result = storage.getVariableHandler("text", expressionResolver)

        assertNull(result)
        assertEquals(listOf("Variable is not an integer variable: text"), reporter.errors)
    }

    @Test
    fun `variable is not updated until the next frame`() {
        storage.attach(testScope)

        width.update(sizeInPixels = 20, density = 1f)
        testScope.testScheduler.runCurrent()

        assertEquals(0L, widthVariable.getValue())
    }

    @Test
    fun `pixel size is rounded to integer dp on the next frame`() {
        storage.attach(testScope)

        width.update(sizeInPixels = 5, density = 2f)
        advanceFrame()

        assertEquals(3L, widthVariable.getValue())
    }

    @Test
    fun `latest size replaces earlier pending updates`() {
        storage.attach(testScope)

        width.update(sizeInPixels = 10, density = 1f)
        width.update(sizeInPixels = 20, density = 1f)
        advanceFrame()

        assertEquals(20L, widthVariable.getValue())
    }

    @Test
    fun `unchanged pixel size preserves an external variable value`() {
        storage.attach(testScope)
        width.update(sizeInPixels = 20, density = 1f)
        advanceFrame()

        widthVariable.set(30)
        width.update(sizeInPixels = 20, density = 1f)
        advanceFrame()

        assertEquals(30L, widthVariable.getValue())
    }

    @Test
    fun `size can become zero after a nonzero update`() {
        storage.attach(testScope)
        width.update(sizeInPixels = 20, density = 1f)
        advanceFrame()

        width.update(sizeInPixels = 0, density = 1f)
        advanceFrame()

        assertEquals(0L, widthVariable.getValue())
    }

    @Test
    fun `forgetting a handler discards its pending update`() {
        storage.attach(testScope)

        width.update(sizeInPixels = 20, density = 1f)
        width.onForgotten()
        advanceFrame()

        assertEquals(0L, widthVariable.getValue())
    }

    @Test
    fun `forgetting one handler preserves another handler update`() {
        val heightVariable = Variable.IntegerVariable("height", 0)
        variableController.declare(heightVariable)

        storage.attach(testScope)

        width.update(sizeInPixels = 20, density = 1f)
        rememberVariableHandler("height").update(sizeInPixels = 40, density = 1f)
        width.onForgotten()
        advanceFrame()

        assertEquals(40L, heightVariable.getValue())
    }

    @Test
    fun `recursive updates stop despite synchronous variable notifications`() {
        storage.attach(testScope)
        widthVariable.addObserver {
            width.update(
                sizeInPixels = (it.getValue() as Long).toInt() + 1,
                density = 1f,
            )
        }
        width.update(sizeInPixels = 1, density = 1f)

        repeat(20) { advanceFrame() }

        assertEquals(3L, widthVariable.getValue())
        assertEquals(listOf("Recursive layout for variable: width"), reporter.warnings)
    }

    @Test
    fun `external notifications allow continuous updates`() {
        storage.attach(testScope)

        repeat(12) { index ->
            widthVariable.set(-1)
            width.update(sizeInPixels = index + 1, density = 1f)
            advanceFrame()
        }

        assertEquals(12L, widthVariable.getValue())
        assertTrue(reporter.warnings.isEmpty())
    }

    @Test
    fun `variables from handlers remembered after attaching are observed`() {
        width.onForgotten()
        storage.attach(testScope)
        val width = rememberVariableHandler("width")

        repeat(12) { index ->
            widthVariable.set(-1)
            width.update(sizeInPixels = index + 1, density = 1f)
            advanceFrame()
        }

        assertEquals(12L, widthVariable.getValue())
        assertTrue(reporter.warnings.isEmpty())
    }

    @Test
    fun `forgetting one handler preserves another handler subscription to the same variable`() {
        val otherWidth = rememberVariableHandler("width")
        storage.attach(testScope)
        otherWidth.onForgotten()

        repeat(12) { index ->
            widthVariable.set(-1)
            width.update(sizeInPixels = index + 1, density = 1f)
            advanceFrame()
        }

        assertEquals(12L, widthVariable.getValue())
        assertTrue(reporter.warnings.isEmpty())
    }

    @Test
    fun `changes to a forgotten handler variable do not reset update counts`() {
        val heightVariable = Variable.IntegerVariable("height", 0)
        variableController.declare(heightVariable)
        val height = rememberVariableHandler("height")
        storage.attach(testScope)
        width.onForgotten()
        heightVariable.addObserver {
            height.update(sizeInPixels = (it.getValue() as Long).toInt() + 1, density = 1f)
        }
        height.update(sizeInPixels = 1, density = 1f)

        repeat(20) { index ->
            widthVariable.set(index + 1L)
            advanceFrame()
        }

        assertEquals(3L, heightVariable.getValue())
        assertEquals(listOf("Recursive layout for variable: height"), reporter.warnings)
    }

    @Test
    fun `changes to another remembered handler variable do not reset update counts`() {
        val heightVariable = Variable.IntegerVariable("height", 0)
        variableController.declare(heightVariable)
        rememberVariableHandler("height")
        storage.attach(testScope)
        widthVariable.addObserver {
            width.update(sizeInPixels = (it.getValue() as Long).toInt() + 1, density = 1f)
        }
        width.update(sizeInPixels = 1, density = 1f)

        repeat(20) { index ->
            heightVariable.set(index + 1L)
            advanceFrame()
        }

        assertEquals(3L, widthVariable.getValue())
        assertEquals(listOf("Recursive layout for variable: width"), reporter.warnings)
    }

    @Test
    fun `creating a handler does not activate it`() {
        width.onForgotten()
        storage.attach(testScope)
        val handler = assertNotNull(storage.getVariableHandler("width", expressionResolver))

        handler.update(sizeInPixels = 20, density = 1f)
        advanceFrame()

        assertEquals(0L, widthVariable.getValue())
    }

    @Test
    fun `attaching storage does not activate a handler that is not remembered`() {
        width.onForgotten()
        val handler = assertNotNull(storage.getVariableHandler("width", expressionResolver))

        storage.attach(testScope)
        handler.update(sizeInPixels = 20, density = 1f)
        advanceFrame()

        assertEquals(0L, widthVariable.getValue())
    }

    @Test
    fun `variable subscriptions resume after remembering a forgotten handler`() {
        storage.attach(testScope)
        width.onForgotten()
        width.onRemembered()

        repeat(12) { index ->
            widthVariable.set(-1)
            width.update(sizeInPixels = index + 1, density = 1f)
            advanceFrame()
        }

        assertEquals(12L, widthVariable.getValue())
        assertTrue(reporter.warnings.isEmpty())
    }

    @Test
    fun `variable subscriptions resume after reattaching`() {
        storage.attach(testScope)
        storage.detach()
        storage.attach(testScope)

        repeat(12) { index ->
            widthVariable.set(-1)
            width.update(sizeInPixels = index + 1, density = 1f)
            advanceFrame()
        }

        assertEquals(12L, widthVariable.getValue())
        assertTrue(reporter.warnings.isEmpty())
    }

    @Test
    fun `new updates are not published after detaching`() {
        storage.attach(testScope)
        storage.detach()

        width.update(sizeInPixels = 20, density = 1f)
        advanceFrame()

        assertEquals(0L, widthVariable.getValue())
    }

    @Test
    fun `detaching discards pending updates`() {
        storage.attach(testScope)
        width.update(sizeInPixels = 20, density = 1f)
        testScope.testScheduler.runCurrent()

        storage.detach()
        advanceFrame()

        assertEquals(0L, widthVariable.getValue())
    }

    @Test
    fun `updates resume after reattaching`() {
        storage.attach(testScope)
        storage.detach()
        storage.attach(testScope)

        width.update(sizeInPixels = 20, density = 1f)
        advanceFrame()

        assertEquals(20L, widthVariable.getValue())
    }

    @Test
    fun `handlers of the same variable publish only the latest size per frame`() {
        storage.attach(testScope)
        val publishedSizes = mutableListOf<Long>()
        widthVariable.addObserver { publishedSizes.add(it.getValue() as Long) }

        repeat(4) { index ->
            rememberVariableHandler("width").update(sizeInPixels = (index + 1) * 10, density = 1f)
        }
        advanceFrame()

        assertEquals(listOf(40L), publishedSizes)
        assertTrue(reporter.warnings.isEmpty())
    }

    @Test
    fun `latest update wins when a handler updates again before the next frame`() {
        val otherWidth = rememberVariableHandler("width")
        storage.attach(testScope)

        width.update(sizeInPixels = 10, density = 1f)
        otherWidth.update(sizeInPixels = 20, density = 1f)
        width.update(sizeInPixels = 30, density = 1f)
        advanceFrame()

        assertEquals(30L, widthVariable.getValue())
    }

    @Test
    fun `forgetting the latest handler preserves another pending update to the same variable`() {
        val otherWidth = rememberVariableHandler("width")
        storage.attach(testScope)

        width.update(sizeInPixels = 10, density = 1f)
        otherWidth.update(sizeInPixels = 20, density = 1f)
        otherWidth.onForgotten()
        advanceFrame()

        assertEquals(10L, widthVariable.getValue())
    }

    @Test
    fun `detaching cancels updates after reattaching with a pending job cancellation`() {
        storage.attach(testScope)
        width.update(sizeInPixels = 10, density = 1f)
        testScope.testScheduler.runCurrent()
        storage.detach()
        storage.attach(testScope)
        width.update(sizeInPixels = 20, density = 1f)
        testScope.testScheduler.runCurrent()

        storage.detach()
        width.update(sizeInPixels = 30, density = 1f)
        advanceFrame()

        assertEquals(0L, widthVariable.getValue())
    }

    private fun rememberVariableHandler(variableName: String): LayoutProviderStorage.VariableHandler {
        return assertNotNull(storage.getVariableHandler(variableName, expressionResolver)).apply {
            onRemembered()
        }
    }

    private fun advanceFrame() {
        testScope.testScheduler.runCurrent()
        frameTimeNanos += 16_000_000
        frameClock.sendFrame(frameTimeNanos)
        testScope.testScheduler.runCurrent()
    }
}
