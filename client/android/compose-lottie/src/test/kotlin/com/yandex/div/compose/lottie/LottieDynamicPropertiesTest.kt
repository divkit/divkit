package com.yandex.div.compose.lottie

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.Constraints
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.LottieResult
import com.airbnb.lottie.compose.LottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.DivView
import com.yandex.div.lottie.DivLottieResourceLoader
import com.yandex.div.test.data.data
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivExtension
import com.yandex.div2.DivImage
import org.json.JSONObject
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class LottieDynamicPropertiesTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `existing constructor loads animation without provider`() {
        val composition = createDynamicComposition()
        val loader = DynamicPropertiesLoader(mapOf(FIRST_URL to composition))
        val fixture = lottieFixture(FIRST_URL)
        val recompose = mutableStateOf(0)

        setContent(
            handler = LottieExtensionHandler(loader),
            data = mutableStateOf(fixture.divData),
            recompose = recompose,
        )
        rule.waitForIdle()

        assertEquals(listOf(FIRST_URL), loader.loadedUrls)

        rule.runOnIdle { recompose.value++ }
        rule.waitForIdle()

        assertEquals(listOf(FIRST_URL), loader.loadedUrls)
    }

    @Test
    fun `provider receives loaded composition data and extension`() {
        val composition = createDynamicComposition()
        val loader = DynamicPropertiesLoader(mapOf(FIRST_URL to composition))
        val fixture = lottieFixture(FIRST_URL)
        var receivedContext: LottieDynamicPropertiesContext? = null
        val provider = object : LottieDynamicPropertiesProvider {
            @Composable
            override fun getDynamicProperties(
                context: LottieDynamicPropertiesContext,
            ): LottieDynamicProperties? {
                receivedContext = context
                return null
            }
        }

        setContent(
            handler = LottieExtensionHandler(loader, provider),
            data = mutableStateOf(fixture.divData),
        )
        rule.waitForIdle()

        val context = requireNotNull(receivedContext)
        assertSame(fixture.div, context.environment.data)
        assertSame(fixture.extension, context.environment.extension)
        assertSame(composition, context.composition)
    }

    @Test
    fun `provider result reacts to host compose state`() {
        val composition = createDynamicComposition()
        val loader = DynamicPropertiesLoader(mapOf(FIRST_URL to composition))
        val fixture = lottieFixture(FIRST_URL)
        val color = mutableStateOf(Color.RED)
        val providedProperties = mutableListOf<LottieDynamicProperties>()
        val provider = object : LottieDynamicPropertiesProvider {
            @Composable
            override fun getDynamicProperties(
                context: LottieDynamicPropertiesContext,
            ): LottieDynamicProperties {
                return dynamicColorProperties(color.value).also(providedProperties::add)
            }
        }

        setContent(
            handler = LottieExtensionHandler(loader, provider),
            data = mutableStateOf(fixture.divData),
        )
        rule.waitForIdle()
        val initialProperties = providedProperties.last()

        rule.runOnIdle { color.value = Color.GREEN }
        rule.waitForIdle()

        assertNotSame(initialProperties, providedProperties.last())
    }

    @Test
    fun `composition change replaces provider properties`() {
        val firstComposition = createDynamicComposition()
        val secondComposition = createDynamicComposition()
        val loader = DynamicPropertiesLoader(
            mapOf(
                FIRST_URL to firstComposition,
                SECOND_URL to secondComposition,
            )
        )
        val firstFixture = lottieFixture(FIRST_URL)
        val secondFixture = lottieFixture(SECOND_URL)
        val currentData = mutableStateOf(firstFixture.divData)
        val providedProperties = mutableListOf<Pair<LottieComposition, LottieDynamicProperties>>()
        val provider = object : LottieDynamicPropertiesProvider {
            @Composable
            override fun getDynamicProperties(
                context: LottieDynamicPropertiesContext,
            ): LottieDynamicProperties {
                return key(context.composition) {
                    val color = if (context.composition === firstComposition) Color.RED else Color.BLUE
                    dynamicColorProperties(color).also {
                        providedProperties += context.composition to it
                    }
                }
            }
        }

        setContent(
            handler = LottieExtensionHandler(loader, provider),
            data = currentData,
        )
        rule.waitForIdle()
        val firstProperties = providedProperties.last().second

        rule.runOnIdle { currentData.value = secondFixture.divData }
        rule.waitForIdle()

        val secondProperties = providedProperties.last { it.first === secondComposition }.second
        assertNotSame(firstProperties, secondProperties)
    }

    private fun setContent(
        handler: LottieExtensionHandler,
        data: State<DivData>,
        modifier: Modifier = Modifier.fixedSize(48, 48),
        recompose: State<Int>? = null,
    ) {
        val errors = mutableListOf<String>()
        val configuration = DivConfiguration(
            extensionHandlers = mapOf("lottie" to handler),
            reporter = object : DivReporter() {
                override fun reportError(message: String) {
                    errors += message
                }
            },
        )
        rule.setContent {
            recompose?.value
            val baseContext = LocalContext.current
            val context = remember(baseContext) { DivContext(baseContext, configuration) }
            CompositionLocalProvider(LocalContext provides context) {
                DivView(data = data.value, modifier = modifier)
            }
        }
        rule.runOnIdle { assertTrue(errors.isEmpty(), errors.joinToString()) }
    }
}

@Composable
private fun dynamicColorProperties(color: Int): LottieDynamicProperties {
    val dynamicProperty = rememberLottieDynamicProperty(
        LottieProperty.COLOR,
        color,
        "Shape Layer 1",
        "Fill 1",
    )
    return rememberLottieDynamicProperties(dynamicProperty)
}

private fun Modifier.fixedSize(width: Int, height: Int): Modifier {
    return layout { measurable, _ ->
        val placeable = measurable.measure(Constraints.fixed(width, height))
        layout(width, height) {
            placeable.place(0, 0)
        }
    }
}

private data class LottieFixture(
    val divData: DivData,
    val div: Div,
    val extension: DivExtension,
)

private fun lottieFixture(
    url: String,
    isPlaying: Boolean = false,
): LottieFixture {
    val extension = DivExtension(
        id = "lottie",
        params = JSONObject()
            .put("lottie_url", url)
            .put("is_playing", isPlaying),
    )
    val div = Div.Image(DivImage(extensions = listOf(extension)))
    return LottieFixture(
        divData = data(div),
        div = div,
        extension = extension,
    )
}

private class DynamicPropertiesLoader(
    private val compositions: Map<String, LottieComposition>,
) : DivLottieResourceLoader {
    val loadedUrls = mutableListOf<String>()

    override fun canLoad(url: String): Boolean = url in compositions

    override suspend fun load(url: String): LottieResult<LottieComposition> {
        loadedUrls += url
        return LottieResult(requireNotNull(compositions[url]))
    }
}

private fun createDynamicComposition(): LottieComposition {
    return requireNotNull(LottieCompositionFactory.fromJsonStringSync(DYNAMIC_LOTTIE, null).value)
}

private const val FIRST_URL = "custom://dynamic-properties-first"
private const val SECOND_URL = "custom://dynamic-properties-second"
private const val DYNAMIC_LOTTIE = """
{
  "v": "5.5.7",
  "fr": 30,
  "ip": 0,
  "op": 1,
  "w": 100,
  "h": 100,
  "layers": [
    {
      "ddd": 0,
      "ind": 1,
      "ty": 4,
      "nm": "Shape Layer 1",
      "sr": 1,
      "ks": {
        "o": { "a": 0, "k": 100 },
        "r": { "a": 0, "k": 0 },
        "p": { "a": 0, "k": [50, 50, 0] },
        "a": { "a": 0, "k": [0, 0, 0] },
        "s": { "a": 0, "k": [100, 100, 100] }
      },
      "shapes": [
        {
          "ty": "rc",
          "d": 1,
          "s": { "a": 0, "k": [50, 50] },
          "p": { "a": 0, "k": [0, 0] },
          "r": { "a": 0, "k": 0 },
          "nm": "Rectangle Path 1"
        },
        {
          "ty": "fl",
          "c": { "a": 0, "k": [1, 0, 0, 1] },
          "o": { "a": 0, "k": 100 },
          "r": 1,
          "nm": "Fill 1"
        }
      ],
      "ip": 0,
      "op": 1,
      "st": 0,
      "bm": 0
    }
  ]
}
"""
