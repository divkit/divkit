// Copyright (c) 2022 Yandex LLC. All rights reserved.

package com.yandex.div.dsl

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.yandex.div.dsl.context.card
import com.yandex.div.dsl.context.define
import com.yandex.div.dsl.context.override
import com.yandex.div.dsl.context.resolve
import com.yandex.div.dsl.context.templates
import com.yandex.div.dsl.model.Div
import com.yandex.div.dsl.model.DivContainer
import com.yandex.div.dsl.model.DivSizeUnit
import com.yandex.div.dsl.model.divAction
import com.yandex.div.dsl.model.divContainer
import com.yandex.div.dsl.model.divData
import com.yandex.div.dsl.model.divFixedSize
import com.yandex.div.dsl.model.divMatchParentSize
import com.yandex.div.dsl.model.divText
import com.yandex.div.dsl.model.divWrapContentSize
import com.yandex.div.dsl.model.state
import com.yandex.div.dsl.model.template
import com.yandex.div.dsl.serializer.toJsonNode
import java.net.URI
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import org.skyscreamer.jsonassert.JSONCompareMode

internal class CardWithTemplatesTest {
    @Test
    internal fun renderCard() {
        val titleRef = reference<String>("title")
        val templates = templates<Div> {
            define ("title_text",
                divText(
                    width = divFixedSize(
                        value = int(120),
                        unit = enum(DivSizeUnit.DP)
                    ),
                    text = titleRef
                )
            )
            define("header",
                divContainer(
                    orientation = enum(DivContainer.Orientation.VERTICAL),
                    items = listOf(
                        divText(
                            width = divWrapContentSize(),
                            height  = divFixedSize(
                                value = int(60),
                            ),
                            text = titleRef
                        ),
                        template(
                            type = "title_text",
                            override("width", divWrapContentSize())
                        )
                    )
                )
            )
            define("subtitle_text",
                template(
                    type = "title_text",
                    override("width", divWrapContentSize())
                )
            )
        }
        val card = card {
            divData(
                logId = "layout",
                states = listOf(
                    state(
                        stateId = 0,
                        div = divContainer(
                            width = divFixedSize(
                                value = 320
                            ),
                            height = divFixedSize(
                                value = 320
                            ),
                            orientation = DivContainer.Orientation.VERTICAL,
                            items = listOf(
                                template(
                                    type = "title_text",
                                    override("width", divMatchParentSize()),
                                    resolve(titleRef, "Good news, everyone!")
                                ),
                                divText(
                                    width = divWrapContentSize(),
                                    height = divWrapContentSize(),
                                    text = "Hello, Everyone!",
                                    actions = listOf(
                                        divAction(
                                            logId = "tap_action",
                                            url = URI("https://yandex.ru")
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }

        val expected = readResource("/json/card_with_templates.json")

        val actual = JsonNodeFactory.instance.objectNode().apply {
            set<ObjectNode>("templates", templates.toJsonNode())
            set<ObjectNode>("card", card.toJsonNode())
        }.toString()

        assertEquals(expected, actual, JSONCompareMode.STRICT)
    }
}
