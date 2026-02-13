package divkit.dsl

import divkit.dsl.core.bind
import divkit.dsl.core.expression
import divkit.dsl.core.reference
import kotlin.test.Test

class TemplatesTest {

    @Test
    fun `multiple items`() {
        val titleRef = reference<String>("title")

        val template = template("item") {
            text(
                maxLines = 1
            ) + textRefs(
                text = titleRef
            )
        }

        val card = divan {
            data(
                logId = "test",
                div = container(
                    orientation = vertical,
                    items = listOf(
                        render(
                            template,
                            titleRef bind "Title 1"
                        ),
                        render(
                            template,
                            titleRef bind "Title 2"
                        )
                    )
                )
            )
        }

        val expectedJson = $$"""
            {
                "templates": {
                    "item": {
                        "type": "text",
                        "max_lines": 1,
                        "$text": "title"
                    }
                },
                "card": {
                    "log_id": "test",
                    "states": [
                        {
                            "state_id": 0,
                            "div": {
                                "type": "container",
                                "items": [
                                    {
                                        "type": "item",
                                        "title": "Title 1"
                                    },
                                    {
                                        "type": "item",
                                        "title": "Title 2"
                                    }
                                ],
                                "orientation": "vertical"
                            }
                        }
                    ]
                }
            }
        """

        assertEquals(expectedJson, card)
    }

    @Test
    fun `override template property`() {
        val titleRef = reference<String>("title")

        val template = template("title_text") {
            text(
                maxLines = 1
            ) + textRefs(
                text = titleRef
            )
        }

        val card = divan {
            data(
                logId = "test",
                div = render(
                    template,
                    titleRef bind "Hello!"
                ) + textProps(maxLines = 2)
            )
        }

        val expectedJson = $$"""
            {
                "templates": {
                    "title_text": {
                        "type": "text",
                        "max_lines": 1,
                        "$text": "title"
                    }
                },
                "card": {
                    "log_id": "test",
                    "states": [
                        {
                            "state_id": 0,
                            "div": {
                                "type": "title_text",
                                "max_lines": 2,
                                "title": "Hello!"
                            }
                        }
                    ]
                }
            }
        """

        assertEquals(expectedJson, card)
    }

    @Test
    fun `expression binding`() {
        val titleRef = reference<String>("title")

        val template = template("title_text") {
            text() + textRefs(
                text = titleRef
            )
        }

        val card = divan {
            data(
                logId = "test",
                div = render(
                    template,
                    titleRef bind expression("@{title}")
                )
            )
        }

        val expectedJson = $$"""
            {
                "templates": {
                    "title_text": {
                        "type": "text",
                        "$text": "title"
                    }
                },
                "card": {
                    "log_id": "test",
                    "states": [
                        {
                            "state_id": 0,
                            "div": {
                                "type": "title_text",
                                "title": "@{title}"
                            }
                        }
                    ]
                }
            }
        """

        assertEquals(expectedJson, card)
    }

    @Test
    fun `template with reference in nested object`() {
        val titleRef = reference<String>("title")

        val template = template("item") {
            row(
                items = listOf(
                    text(
                        maxLines = 1
                    ) + textRefs(text = titleRef)
                )
            )
        }

        val card = divan {
            data(
                logId = "test",
                div = render(
                    template,
                    titleRef bind "Hello!"
                )
            )
        }

        val expectedJson = $$"""
            {
                "templates": {
                    "item": {
                        "type": "container",
                        "items": [
                            {
                                "type": "text",
                                "max_lines": 1,
                                "$text": "title"
                            }
                        ],
                        "orientation": "horizontal"
                    }
                },
                "card": {
                    "log_id": "test",
                    "states": [
                        {
                            "state_id": 0,
                            "div": {
                                "type": "item",
                                "title": "Hello!"
                            }
                        }
                    ]
                }
            }
        """

        assertEquals(expectedJson, card)
    }

    @Test
    fun `templates hierarchy`() {
        val titleRef = reference<String>("title")

        val baseTemplate = template("base_text") {
            text(
                maxLines = 1
            )
        }

        val template = template("title_text") {
            render(
                baseTemplate
            ) + textRefs(
                text = titleRef
            )
        }

        val card = divan {
            data(
                logId = "test",
                div = render(
                    template,
                    titleRef bind "Hello!"
                )
            )
        }

        val expectedJson = $$"""
            {
                "templates": {
                    "base_text": {
                        "type": "text",
                        "max_lines": 1
                    },
                    "title_text": {
                        "type": "base_text",
                        "$text": "title"
                    }
                },
                "card": {
                    "log_id": "test",
                    "states": [
                        {
                            "state_id": 0,
                            "div": {
                                "type": "title_text",
                                "title": "Hello!"
                            }
                        }
                    ]
                }
            }
        """

        assertEquals(expectedJson, card)
    }
}
