package com.yandex.divkit.example.dsl

import com.yandex.divkit.example.dsl.data.Variables
import divkit.dsl.Url
import divkit.dsl.action
import divkit.dsl.actionSetVariable
import divkit.dsl.center
import divkit.dsl.container
import divkit.dsl.core.reference
import divkit.dsl.defer
import divkit.dsl.edgeInsets
import divkit.dsl.fixedSize
import divkit.dsl.image
import divkit.dsl.stringValue
import divkit.dsl.template
import divkit.dsl.text

object CardTemplate {
    val descriptionRef = reference<String>("description")
    val imageRef = reference<Url>("img")

    val template by lazy(LazyThreadSafetyMode.NONE) {
        template("sample_card") {
            container(
                margins = edgeInsets(all = 10),
                width = fixedSize(
                    value = 150,
                ),
                items = listOf(
                    image().defer(
                        imageUrl = imageRef,
                    ),
                    text(
                        textAlignmentHorizontal = center,
                    ).defer(
                        text = descriptionRef,
                    )
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
                )
            )
        }
    }
}
