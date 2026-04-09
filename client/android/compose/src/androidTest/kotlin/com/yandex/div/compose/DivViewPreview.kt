package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.yandex.div.test.data.action
import com.yandex.div.test.data.color
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.solidBackground
import com.yandex.div.test.data.text
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.variable
import com.yandex.div2.DivEdgeInsets

@Preview
@Composable
fun DivViewPreview() {
    val divContext = DivContext(
        baseContext = LocalContext.current,
        configuration = DivComposeConfiguration()
    )
    CompositionLocalProvider(LocalContext provides divContext) {
        DivView(data = testData)
    }
}

private val testData = data(
    content = container(
        backgrounds = listOf(
            solidBackground(color = color(0xFF90C090))
        ),
        items = listOf(
            text(
                action = action(
                    typed = setVariableAction(
                        name = "title",
                        value = typedValue("New Title")
                    )
                ),
                fontSize = 36,
                text = expression("@{title}"),
                textColor = color(0xFFBF0000)
            )
        ),
        margins = DivEdgeInsets(
            top = constant(10),
            bottom = constant(10)
        ),
        paddings = DivEdgeInsets(
            start = constant(10),
            end = constant(10),
            top = constant(20),
            bottom = constant(20)
        )
    ),
    variables = listOf(
        variable(name = "title", value = "Hello!")
    )
)
