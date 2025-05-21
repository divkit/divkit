package com.yandex.divkit.example.dsl

import com.yandex.divkit.example.dsl.data.Variables
import divkit.dsl.Div
import divkit.dsl.Url
import divkit.dsl.action
import divkit.dsl.actionSetVariable
import divkit.dsl.center
import divkit.dsl.container
import divkit.dsl.core.reference
import divkit.dsl.defer
import divkit.dsl.edgeInsets
import divkit.dsl.fixedSize
import divkit.dsl.horizontal
import divkit.dsl.image
import divkit.dsl.matchParentSize
import divkit.dsl.separator
import divkit.dsl.space_around
import divkit.dsl.stringValue
import divkit.dsl.template
import divkit.dsl.text

object CardTemplate {
    val descriptionRef = reference<String>("description")
    val imageRef = reference<Url>("img")
    val buttonsRef = reference<List<Div>>("buttons")

    val template by lazy(LazyThreadSafetyMode.NONE) {
        template("sample_card") {
            container(
                margins = edgeInsets(all = 10),
                width = fixedSize(
                    value = 150,
                ),
                items = listOf(
                    container(
                        items = listOf(
                            image().defer(
                                imageUrl = imageRef,
                            ),
                            text(
                                textAlignmentHorizontal = center,
                            ).defer(
                                text = descriptionRef,
                            ),
                        ),
                        actions = listOf(
                            action(
                                logId = "post_selected",
                                typed = actionSetVariable(
                                    variableName = Variables.SELECTED_POST_DESCRIPTION,
                                    value = stringValue().defer(
                                        value = descriptionRef,
                                    )
                                )
                            )
                        ),
                    ),
                    separator(
                        width = matchParentSize(),
                        height = fixedSize(1),
                        margins = edgeInsets(
                            top = 1,
                            bottom = 4,
                        ),
                    ),
                    text(
                        text = "Share",
                        textAlignmentHorizontal = center,
                        margins = edgeInsets(
                            bottom = 4,
                        ),
                    ),
                    container(
                        orientation = horizontal,
                        width = matchParentSize(),
                        contentAlignmentHorizontal = space_around,
                    ).defer(
                        items = buttonsRef,
                    )
                ),
            )
        }
    }
}
