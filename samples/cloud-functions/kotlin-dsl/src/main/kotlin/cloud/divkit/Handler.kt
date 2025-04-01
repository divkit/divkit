package cloud.divkit

import divkit.dsl.Divan
import divkit.dsl.bold
import divkit.dsl.border
import divkit.dsl.center
import divkit.dsl.color
import divkit.dsl.container
import divkit.dsl.data
import divkit.dsl.divan
import divkit.dsl.edgeInsets
import divkit.dsl.fixedSize
import divkit.dsl.medium
import divkit.dsl.solidBackground
import divkit.dsl.text
import divkit.dsl.vertical
import divkit.dsl.wrapContentSize

fun handle(request: Request): Response {
    val card = greetingCard()
    return Response(200, card.toJson())
}

private fun greetingCard(): Divan {
    return divan {
        data(
            logId = "greeting",
            div = container(
                width = wrapContentSize(),
                height = wrapContentSize(),
                orientation = vertical,
                margins = edgeInsets(
                    start = 16,
                    top = 16,
                    end = 16,
                    bottom = 16
                ),
                paddings = edgeInsets(
                    start = 4,
                    top = 4,
                    end = 4,
                    bottom = 4
                ),
                background = listOf(
                    solidBackground(color("#FF9000"))
                ),
                border = border(
                    cornerRadius = 8
                ),
                items = listOf(
                    text(
                        width = wrapContentSize(),
                        height = wrapContentSize(),
                        alignmentHorizontal = center,
                        fontSize = 20,
                        fontWeight = bold,
                        textColor = color("#FFFFFF"),
                        text = "HELLO"
                    ),
                    text(
                        width = wrapContentSize(),
                        height = wrapContentSize(),
                        alignmentHorizontal = center,
                        fontSize = 14,
                        textColor = color("#FFFFFF"),
                        text = "my name is"
                    ),
                    text(
                        width = fixedSize(120),
                        height = fixedSize(48),
                        margins = edgeInsets(
                            top = 4
                        ),
                        background = listOf(
                            solidBackground(color("#FFFFFF"))
                        ),
                        border = border(
                            cornerRadius = 4
                        ),
                        fontSize = 32,
                        fontWeight = medium,
                        textAlignmentHorizontal = center,
                        textAlignmentVertical = center,
                        text = "DivKit"
                    )
                )
            )
        )
    }
}
