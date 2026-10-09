package com.yandex.div.compose.storedvalues

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.data.StoredValue
import com.yandex.div.data.StoredValue.StringStoredValue
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.separator
import com.yandex.div.test.data.setStoredValueAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.typedValue
import com.yandex.div2.DivActionSetStoredValue
import com.yandex.div2.DivExtension
import com.yandex.div2.DivVariable
import com.yandex.div2.StrVariable
import org.junit.Rule
import org.junit.runner.RunWith
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class DivStoredValuesStorageIntegrationTest {

    @get:Rule
    val rule = createComposeRule()

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `value written before first composition initializes root variable`() {
        val valueName = uniqueName("initial")
        val card = data(
            content = text(text = expression("@{root_value}")),
            variables = listOf(
                DivVariable.Str(
                    StrVariable(
                        name = "root_value",
                        value = expression(
                            "@{getStoredStringValue('$valueName', 'card', 'fallback')}"
                        ),
                    )
                )
            ),
        ).copy(logId = uniqueName("card"))
        val divContext = createContext()

        divContext.getStoredValuesStorage(card.logId).setValue(
            value = StringStoredValue(valueName, "stored before composition"),
            scope = DivStoredValueScope.Card,
            lifetime = ONE_HOUR,
        )
        setContent(divContext, card)

        rule.onNodeWithText("stored before composition").assertIsDisplayed()
    }

    @Test
    fun `set stored value action is visible through public storage`() {
        val valueName = uniqueName("action")
        val card = data(
            content = text(
                action = action(
                    typed = setStoredValueAction(
                        name = valueName,
                        value = typedValue("stored by action"),
                        scope = DivActionSetStoredValue.Scope.CARD,
                        lifetime = ONE_HOUR,
                    )
                ),
                id = "button",
                text = constant("store"),
            )
        ).copy(logId = uniqueName("card"))
        val divContext = createContext()
        val storage = divContext.getStoredValuesStorage(card.logId)

        setContent(divContext, card)
        rule.onNodeWithTag("button").performClick()

        assertEquals(
            StringStoredValue(valueName, "stored by action"),
            storage.getStoredValue(valueName, DivStoredValueScope.Card),
        )
    }

    @Test
    fun `card scope is isolated and global scope is shared between cards`() {
        val valueName = uniqueName("scopes")
        val firstCard = data(content = separator()).copy(logId = uniqueName("first_card"))
        val secondCard = data(content = separator()).copy(logId = uniqueName("second_card"))
        val divContext = createContext()
        val firstStorage = divContext.getStoredValuesStorage(firstCard.logId)
        val secondStorage = divContext.getStoredValuesStorage(secondCard.logId)

        firstStorage.setValue(
            StringStoredValue(valueName, "first card"),
            DivStoredValueScope.Card,
            ONE_HOUR,
        )
        firstStorage.setValue(
            StringStoredValue(valueName, "global"),
            DivStoredValueScope.Global,
            ONE_HOUR,
        )

        assertEquals(
            StringStoredValue(valueName, "first card"),
            firstStorage.getStoredValue(valueName, DivStoredValueScope.Card),
        )
        assertNull(secondStorage.getStoredValue(valueName, DivStoredValueScope.Card))
        assertEquals(
            StringStoredValue(valueName, "global"),
            secondStorage.getStoredValue(valueName, DivStoredValueScope.Global),
        )
    }

    @Test
    fun `custom and extension environments use current card storage`() {
        val valueName = uniqueName("environment")
        var customValue: StoredValue? = null
        var extensionValue: StoredValue? = null
        val configuration = DivConfiguration(
            customViewFactories = mapOf(
                "storage" to object : DivCustomViewFactory {
                    @Composable
                    override fun Content(modifier: Modifier, environment: DivCustomEnvironment) {
                        customValue = environment.storedValuesStorage.getStoredValue(
                            valueName,
                            DivStoredValueScope.Card,
                        )
                    }
                }
            ),
            extensionHandlers = mapOf(
                "storage" to object : DivExtensionHandler {
                    @Composable
                    override fun Content(
                        modifier: Modifier,
                        environment: DivExtensionEnvironment,
                        content: @Composable (Modifier) -> Unit,
                    ) {
                        extensionValue = environment.storedValuesStorage.getStoredValue(
                            valueName,
                            DivStoredValueScope.Card,
                        )
                        content(modifier)
                    }
                }
            ),
        )
        val card = data(
            content = container(
                items = listOf(
                    custom(type = "storage"),
                    separator(extensions = listOf(DivExtension(id = "storage"))),
                )
            )
        ).copy(logId = uniqueName("card"))
        val divContext = createContext(configuration)
        val expected = StringStoredValue(valueName, "card value")
        divContext.getStoredValuesStorage(card.logId).setValue(
            expected,
            DivStoredValueScope.Card,
            ONE_HOUR,
        )

        setContent(divContext, card)
        rule.waitForIdle()

        assertEquals(expected, customValue)
        assertEquals(expected, extensionValue)
    }

    private fun createContext(configuration: DivConfiguration = DivConfiguration()): DivContext {
        return DivContext(applicationContext, configuration)
    }

    private fun setContent(divContext: DivContext, data: com.yandex.div2.DivData) {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data)
            }
        }
    }

    private fun uniqueName(prefix: String): String = "$prefix-${UUID.randomUUID()}"
}

private const val ONE_HOUR = 3600L
