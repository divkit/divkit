package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.yandex.div.internal.parser.TYPE_HELPER_STRING
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivBackground
import com.yandex.div2.DivContainer
import com.yandex.div2.DivData
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivImage
import com.yandex.div2.DivSize
import com.yandex.div2.DivSolidBackground
import com.yandex.div2.DivText
import com.yandex.div2.DivVariable
import com.yandex.div2.StrVariable

@Preview
@Composable
private fun DivViewPreview() {
    val divContext = DivComposeConfiguration()
        .createContext(baseContext = LocalContext.current)
    CompositionLocalProvider(LocalContext provides divContext) {
        DivView(data = testData)
    }
}

private val testData = DivData(
    logId = "preview",
    variables = listOf(
        DivVariable.Str(
            StrVariable(
                name = "title",
                value = constant("Hello!")
            )
        )
    ),
    states = listOf(
        DivData.State(
            stateId = 0,
            div = Div.Container(
                value = DivContainer(
                    orientation = constant(DivContainer.Orientation.VERTICAL),
                    paddings = DivEdgeInsets(
                        start = constant(10),
                        end = constant(10),
                        top = constant(20),
                        bottom = constant(20)
                    ),
                    margins = DivEdgeInsets(
                        top = constant(10),
                        bottom = constant(10)
                    ),
                    background = listOf(
                        DivBackground.Solid(
                            value = DivSolidBackground(
                                color = constant(0xFF909090.toInt()),
                            )
                        )
                    ),
                    items = listOf(
                        Div.Image(
                            value = DivImage(
                                width = fixedSize(48),
                                height = fixedSize(48),
                                imageUrl = constant("https://yastatic.net/s3/home/divkit/logo.png".toUri())
                            )
                        ),
                        Div.Text(
                            value = DivText(
                                fontSize = constant(36),
                                text = expression("@{title}"),
                                textColor = constant(0xFFBF0000.toInt()),
                            )
                        )
                    )
                )
            )
        )
    )
)

private fun <T : Any> constant(value: T): Expression<T> {
    return Expression.ConstantExpression(value)
}

private fun expression(expression: String): Expression<String> {
    return Expression.MutableExpression<String, String>(
        expressionKey = "text",
        rawExpression = expression,
        converter = null,
        validator = { true },
        logger = ParsingErrorLogger.ASSERT,
        typeHelper = TYPE_HELPER_STRING
    )
}

private fun fixedSize(value: Long): DivSize {
    return DivSize.Fixed(
        value = DivFixedSize(value = constant(value))
    )
}
