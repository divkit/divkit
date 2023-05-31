package divkit.dsl

import com.fasterxml.jackson.databind.json.JsonMapper
import divkit.dsl.core.bind
import divkit.dsl.core.reference
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import org.skyscreamer.jsonassert.JSONCompareMode

class DivanTest {

    @Test
    fun buildCardWithTemplate() {
        val titleRef = reference<String>("title")

        val titleTemplate = template("title_text") {
            text(
                width = fixedSize(
                    value = 120,
                    unit = dp
                )
            ) + textRefs(text = titleRef)
        }

        val divan = divan {
            data(
                logId = "layout",
                states = singleRoot(
                    div = container(
                        width = fixedSize(
                            value = 320
                        ),
                        height = fixedSize(
                            value = 320
                        ),
                        orientation = vertical,
                        items = listOf(
                            render(
                                titleTemplate,
                                titleRef bind "Good news, everyone!"
                            ) + textProps(
                                width = matchParentSize()
                            ),
                            text(
                                width = wrapContentSize(),
                                height = wrapContentSize(),
                                text = "Hello, Everyone!",
                                actions = listOf(
                                    action(
                                        logId = "tap_action",
                                        url = url("https://yandex.ru")
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }

        val expected = readResource("/json/card_with_templates.json")
        val actual = JsonMapper.builder().build().writeValueAsString(divan)
        assertEquals(expected, actual, JSONCompareMode.STRICT)
    }
}
