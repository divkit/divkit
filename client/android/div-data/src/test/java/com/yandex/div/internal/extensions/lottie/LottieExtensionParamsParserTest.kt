package com.yandex.div.internal.extensions.lottie

import android.net.Uri
import com.yandex.div.json.expressions.ExpressionResolver
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LottieExtensionParamsParserTest {
    private val assetMap = mutableMapOf<String, String>()
    private val rawResMap = mutableMapOf<String, Int>()
    private val expressionResolver = ExpressionResolver.EMPTY

    private val parser = LottieExtensionParamsParser(
        assetMapper = { assetMap[it] },
        rawResMapper = { rawResMap[it] },
        reportError = {
            if (!isErrorExpected) {
                fail(it)
            }
        }
    )

    private var isErrorExpected = false

    @Test
    fun `parse() returns null for empty json`() {
        assertNull(parser.parse(JSONObject(), expressionResolver))
    }

    @Test
    fun `parse() with lottie_url`() {
        val json = JSONObject()
            .put("lottie_url", "https://example.com/animation.json")

        assertEquals(
            LottieExtensionParams(
                data = LottieData.Url("https://example.com/animation.json"),
                isPlaying = null,
                repeatCount = 1,
                repeatMode = LottieRepeatMode.RESTART,
                repeats = emptyList(),
            ),
            parser.parse(json, expressionResolver)
        )
    }

    @Test
    fun `parse() with asset url maps url`() {
        assetMap["asset://local_animation.json"] = "mapped.json"

        val json = JSONObject()
            .put("lottie_url", "asset://local_animation.json")

        assertEquals(
            LottieExtensionParams(
                data = LottieData.Asset("mapped.json"),
                isPlaying = null,
                repeatCount = 1,
                repeatMode = LottieRepeatMode.RESTART,
                repeats = emptyList(),
            ),
            parser.parse(json, expressionResolver)
        )
    }

    @Test
    fun `parse() with asset url fails if mapper returns null`() {
        isErrorExpected = true

        val json = JSONObject()
            .put("lottie_url", "asset://local_animation.json")

        assertNull(parser.parse(json, expressionResolver))
    }

    @Test
    fun `parse() with divkit-asset url`() {
        val json = JSONObject()
            .put("lottie_url", "divkit-asset://local_animation.json")

        assertEquals(
            LottieExtensionParams(
                data = LottieData.Asset("divkit/local_animation.json"),
                isPlaying = null,
                repeatCount = 1,
                repeatMode = LottieRepeatMode.RESTART,
                repeats = emptyList(),
            ),
            parser.parse(json, expressionResolver)
        )
    }

    @Test
    fun `parse() with res url maps resource`() {
        rawResMap["res://local_animation.json"] = 123

        val json = JSONObject()
            .put("lottie_url", "res://local_animation.json")

        assertEquals(
            LottieExtensionParams(
                data = LottieData.RawRes(id = 123, url = "res://local_animation.json"),
                isPlaying = null,
                repeatCount = 1,
                repeatMode = LottieRepeatMode.RESTART,
                repeats = emptyList(),
            ),
            parser.parse(json, expressionResolver)
        )
    }

    @Test
    fun `parse() with res url fails if mapper returns null`() {
        isErrorExpected = true

        val json = JSONObject()
            .put("lottie_url", "res://local_animation.json")

        assertNull(parser.parse(json, expressionResolver))
    }

    @Test
    fun `parse() with lottie_json`() {
        val json = JSONObject()
            .put("lottie_json", JSONObject().put("key", "value"))

        assertEquals(
            LottieExtensionParams(
                data = LottieData.Json("{\"key\":\"value\"}"),
                isPlaying = null,
                repeatCount = 1,
                repeatMode = LottieRepeatMode.RESTART,
                repeats = emptyList(),
            ),
            parser.parse(json, expressionResolver)
        )
    }

    @Test
    fun `parse() prefers lottie_url over lottie_json`() {
        val json = JSONObject()
            .put("lottie_json", JSONObject().put("key", "value"))
            .put("lottie_url", "https://example.com/animation.json")

        assertEquals(
            LottieExtensionParams(
                data = LottieData.Url("https://example.com/animation.json"),
                isPlaying = null,
                repeatCount = 1,
                repeatMode = LottieRepeatMode.RESTART,
                repeats = emptyList(),
            ),
            parser.parse(json, expressionResolver)
        )
    }

    @Test
    fun `parse() with repeat_count and repeat_mode`() {
        val json = JSONObject()
            .put("lottie_url", "https://example.com/animation.json")
            .put("repeat_count", 4)
            .put("repeat_mode", "reverse")

        assertEquals(
            LottieExtensionParams(
                data = LottieData.Url("https://example.com/animation.json"),
                isPlaying = null,
                repeatCount = 4,
                repeatMode = LottieRepeatMode.REVERSE,
                repeats = emptyList(),
            ),
            parser.parse(json, expressionResolver)
        )
    }

    @Test
    fun `parse() uses defaults for invalid values`() {
        isErrorExpected = true

        val json = JSONObject()
            .put("lottie_url", "https://example.com/animation.json")
            .put("repeat_count", "invalid")
            .put("repeat_mode", "invalid")

        assertEquals(
            LottieExtensionParams(
                data = LottieData.Url("https://example.com/animation.json"),
                isPlaying = null,
                repeatCount = 1,
                repeatMode = LottieRepeatMode.RESTART,
                repeats = emptyList(),
            ),
            parser.parse(json, expressionResolver)
        )
    }

    @Test
    fun `parse() with is_playing`() {
        val json = JSONObject()
            .put("lottie_url", "https://example.com/animation.json")
            .put("is_playing", true)

        val params = parser.parse(json, expressionResolver)!!

        assertTrue(params.isPlaying!!.evaluate(expressionResolver))
    }

    @Test
    fun `parse() with repeats`() {
        val repeatJson = JSONObject()
            .put("repeat_count", 3)
            .put("repeat_mode", "reverse")
            .put("min_frame", 123)
            .put("max_frame", 321)
        val json = JSONObject()
            .put("lottie_url", "https://example.com/animation.json")
            .put("repeats", JSONArray().put(repeatJson))

        assertEquals(
            LottieExtensionParams(
                data = LottieData.Url("https://example.com/animation.json"),
                isPlaying = null,
                repeatCount = 1,
                repeatMode = LottieRepeatMode.RESTART,
                repeats = listOf(
                    LottieRepeat(
                        count = 3,
                        mode = LottieRepeatMode.REVERSE,
                        minFrame = 123,
                        maxFrame = 321
                    )
                ),
            ),
            parser.parse(json, expressionResolver)
        )
    }

    @Test
    fun `parse() skips invalid repeats entries`() {
        val json = JSONObject()
            .put("lottie_url", "https://example.com/animation.json")
            .put(
                "repeats",
                JSONArray()
                    .put("invalid")
                    .put(JSONObject().put("repeat_count", 3))
            )

        assertEquals(
            LottieExtensionParams(
                data = LottieData.Url("https://example.com/animation.json"),
                isPlaying = null,
                repeatCount = 1,
                repeatMode = LottieRepeatMode.RESTART,
                repeats = listOf(
                    LottieRepeat(
                        count = 3,
                        mode = LottieRepeatMode.RESTART,
                        minFrame = null,
                        maxFrame = null
                    )
                ),
            ),
            parser.parse(json, expressionResolver)
        )
    }

    @Test
    fun `parseUrl() returns parsed uri`() {
        val json = JSONObject().put("lottie_url", "https://example.com/animation.json")

        assertEquals(
            Uri.parse("https://example.com/animation.json"),
            parser.parseUrl(json, expressionResolver)
        )
    }

    @Test
    fun `parseUrl() returns null when key missing`() {
        assertNull(parser.parseUrl(JSONObject(), expressionResolver))
    }
}
