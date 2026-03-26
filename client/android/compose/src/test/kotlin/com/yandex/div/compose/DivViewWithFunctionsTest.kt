package com.yandex.div.compose

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivEvaluableType
import com.yandex.div2.DivFunction
import com.yandex.div2.DivFunctionArgument
import com.yandex.div2.DivVariable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewWithFunctionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()

    private val configuration = DivComposeConfiguration(
        reporter = TestReporter(),
        variableController = variableController
    )

    @Test
    fun `expression with DivData-level function`() {
        setContent(
            text(
                id = "title",
                text = expression("getMessage() = '@{getMessage()}'")
            ),
            functions = listOf(getMessageFunction("@{'Hello!'}"))
        )

        rule.onNodeWithTag("title").assertTextEquals("getMessage() = 'Hello!'")
    }

    @Test
    fun `container local function is available in items`() {
        setContent(
            container(
                items = listOf(
                    text(
                        id = "title",
                        text = expression("getMessage() = '@{getMessage()}'")
                    )
                )
            ),
            functions = listOf(getMessageFunction("@{'Hello!'}"))
        )

        rule.onNodeWithTag("title").assertTextEquals("getMessage() = 'Hello!'")
    }

    @Test
    fun `local function shadows parent local function`() {
        setContent(
            text(
                functions = listOf(getMessageFunction("@{'Hello from div-text!'}")),
                id = "title",
                text = expression("getMessage() = '@{getMessage()}'")
            ),
            functions = listOf(getMessageFunction("@{'Hello!'}")),
        )

        rule.onNodeWithTag("title").assertTextEquals("getMessage() = 'Hello from div-text!'")
    }

    @Test
    fun `local function shadows system function`() {
        setContent(
            text(
                functions = listOf(
                    DivFunction(
                        arguments = listOf(
                            DivFunctionArgument(
                                name = "value",
                                type = DivEvaluableType.STRING
                            )
                        ),
                        body = "@{20}",
                        name = "len",
                        returnType = DivEvaluableType.INTEGER
                    )
                ),
                id = "title",
                text = expression("len('123') = @{len('123')}")
            )
        )

        rule.onNodeWithTag("title").assertTextEquals("len('123') = 20")
    }

    private fun setContent(
        content: Div,
        functions: List<DivFunction>? = null,
        variables: List<DivVariable>? = null
    ) {
        rule.setContent(
            configuration = configuration,
            data = data(content, functions = functions, variables = variables)
        )
    }
}

private fun getMessageFunction(body: String): DivFunction {
    return DivFunction(
        arguments = listOf(),
        body = body,
        name = "getMessage",
        returnType = DivEvaluableType.STRING
    )
}
