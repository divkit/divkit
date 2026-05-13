package com.yandex.div.compose.views.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.DivModelInternalApi
import com.yandex.div.data.Variable
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.expression
import com.yandex.div2.DivInputFilter
import com.yandex.div2.DivInputFilterExpression
import com.yandex.div2.DivInputFilterRegex
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(DivModelInternalApi::class)
@RunWith(AndroidJUnit4::class)
class DivInputFilterTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val reporter = TestReporter()
    private val variableController = DivVariableController()

    private val localComponent = mockLocalComponent(
        reporter = reporter,
        variableController = variableController,
    )

    @Test
    fun `regex accepts matching value`() {
        val source = mutableStateOf("")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source = source,
                filters = listOf(regexFilter("[0-9]+"))
            )
        }

        filtered?.value = "12345"
        composeRule.waitForIdle()

        assertEquals("12345", filtered?.value)
        assertEquals("12345", source.value)
    }

    @Test
    fun `regex rejects non-matching value and keeps previous`() {
        val source = mutableStateOf("")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source = source,
                filters = listOf(regexFilter("[0-9]+"))
            )
        }

        filtered?.value = "123"
        composeRule.waitForIdle()
        filtered?.value = "abc"
        composeRule.waitForIdle()

        assertEquals("123", filtered?.value)
        assertEquals("123", source.value)
    }

    @Test
    fun `multiple regexes - all must pass`() {
        val source = mutableStateOf("")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source = source,
                filters = listOf(
                    regexFilter(".*[0-9].*"),
                    regexFilter(".{3,}")
                )
            )
        }

        filtered?.value = "ab1"
        composeRule.waitForIdle()
        assertEquals("ab1", filtered?.value)

        filtered?.value = "a1"
        composeRule.waitForIdle()
        assertEquals("ab1", filtered?.value)
    }

    @Test
    fun `expression true accepts`() {
        val source = mutableStateOf("")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source = source,
                filters = listOf(expressionFilter(true))
            )
        }

        filtered?.value = "anything"
        composeRule.waitForIdle()

        assertEquals("anything", filtered?.value)
    }

    @Test
    fun `expression false rejects`() {
        val source = mutableStateOf("initial")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source = source,
                filters = listOf(expressionFilter(false))
            )
        }

        filtered?.value = "anything"
        composeRule.waitForIdle()

        assertEquals("initial", filtered?.value)
    }

    @Test
    fun `combined regex and expression - both must pass`() {
        val source = mutableStateOf("")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source = source,
                filters = listOf(
                    regexFilter("[a-z]+"),
                    expressionFilter(true)
                )
            )
        }

        filtered?.value = "hello"
        composeRule.waitForIdle()
        assertEquals("hello", filtered?.value)

        filtered?.value = "hello123"
        composeRule.waitForIdle()
        assertEquals("hello", filtered?.value)
    }

    @Test
    fun `combined - expression false rejects even if regex matches`() {
        val source = mutableStateOf("")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source = source,
                filters = listOf(
                    regexFilter("[a-z]+"),
                    expressionFilter(false)
                )
            )
        }

        filtered?.value = "hello"
        composeRule.waitForIdle()

        assertEquals("", filtered?.value)
    }

    @Test
    fun `external source update applied when matches filter`() {
        val source = mutableStateOf("")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source = source,
                filters = listOf(regexFilter("[0-9]+"))
            )
        }

        source.value = "999"
        composeRule.waitForIdle()

        assertEquals("999", filtered?.value)
    }

    @Test
    fun `external source update ignored when fails filter`() {
        val source = mutableStateOf("")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source = source,
                filters = listOf(regexFilter("[0-9]+"))
            )
        }

        filtered?.value = "111"
        composeRule.waitForIdle()

        source.value = "abc"
        composeRule.waitForIdle()

        assertEquals("111", filtered?.value)
    }

    @Test
    fun `invalid regex pattern reports error and is skipped`() {
        reporter.failOnError = false
        val source = mutableStateOf("")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source = source,
                filters = listOf(regexFilter("[invalid"))
            )
        }

        filtered?.value = "anything"
        composeRule.waitForIdle()

        assertEquals("anything", filtered?.value)
        assertEquals("Invalid regex pattern '[invalid'", reporter.lastError)
    }

    @Test
    fun `regex filter rebuilds when pattern variable changes`() {
        val patternVar = Variable.StringVariable("pattern", "[0-9]+")
        variableController.declare(patternVar)
        val source = mutableStateOf("")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source,
                listOf(DivInputFilter.Regex(DivInputFilterRegex(pattern = expression("@{pattern}"))))
            )
        }

        source.value = "12345"
        composeRule.waitForIdle()
        assertEquals("12345", filtered?.value)

        patternVar.set("[a-z]+")
        composeRule.waitForIdle()

        assertEquals("12345", filtered?.value)

        source.value = "67890"
        composeRule.waitForIdle()
        assertEquals("12345", filtered?.value)

        source.value = "hello"
        composeRule.waitForIdle()
        assertEquals("hello", filtered?.value)
    }

    @Test
    fun `expression filter reflects condition variable change`() {
        val allowVar = Variable.BooleanVariable("allow", true)
        variableController.declare(allowVar)
        val source = mutableStateOf("")
        var filtered: MutableState<String>? = null
        setContent {
            filtered = rememberFilteredState(
                source,
                listOf(
                    DivInputFilter.Expression(
                        DivInputFilterExpression(
                            condition = booleanExpression("@{allow}")
                        )
                    )
                )
            )
        }

        source.value = "first"
        composeRule.waitForIdle()
        assertEquals("first", filtered?.value)

        allowVar.set(false)
        source.value = "second"
        composeRule.waitForIdle()
        assertEquals("first", filtered?.value)

        allowVar.set(true)
        source.value = "third"
        composeRule.waitForIdle()
        assertEquals("third", filtered?.value)
    }

    private fun regexFilter(pattern: String): DivInputFilter =
        DivInputFilter.Regex(DivInputFilterRegex(pattern = constant(pattern)))

    private fun expressionFilter(condition: Boolean): DivInputFilter =
        DivInputFilter.Expression(DivInputFilterExpression(condition = constant(condition)))

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                content()
            }
        }
    }
}
