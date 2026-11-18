package com.yandex.div.compose.views.gallery

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.text
import com.yandex.div2.DivContainer
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class GalleryListViewTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val configuration = DivConfiguration(reporter = TestReporter())

    @Test
    fun `gallery in wrap content container with match parent sibling shows items`() {
        composeRule.setContent(
            configuration = configuration,
            data = data(
                container(
                    items = listOf(
                        text(height = matchParent(), text = "stretched"),
                        text(height = fixed(100), text = "sizing"),
                        gallery(items = listOf(text(text = "item"))),
                    ),
                    orientation = DivContainer.Orientation.HORIZONTAL,
                )
            ),
        )

        composeRule.onNodeWithText("item").assertIsDisplayed()
    }
}
