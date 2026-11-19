package com.yandex.div.compose.views.pager

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContent
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.text
import com.yandex.div.test.data.wrapContent
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class DivPagerViewTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val configuration = divConfiguration { reporter = TestReporter() }

    @Test
    fun `match parent pager in wrap content container settles`() {
        composeRule.setContent(
            configuration = configuration,
            data = data(
                container(
                    items = listOf(pager(id = "pager", items = listOf(text(text = "page")))),
                    width = wrapContent(),
                )
            ),
        )

        composeRule.onNodeWithTag("pager").assertExists()
    }
}
