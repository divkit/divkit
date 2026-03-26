package com.yandex.div.lottie

import android.widget.ImageView
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
class DivLottiePreloadUrlCapturingTest {
    @Test
    fun `preload captures at all unique urls`() = runTest {
        val uniqueUrls = listOf(
            "https://assets9.lottiefiles.com/packages/lf20_qn2snk.json",
            "https://raw.githubusercontent.com/thesvbd/Lottie-examples/master/assets/animations/menu.json",
            "https://assets9.lottiefiles.com/packages/lf20_edpg3c3s.json",
        )

        val urls = preloadAndCollectUrls(loadDivData())
        Assert.assertArrayEquals("Actual preload urls: $urls",
            uniqueUrls.sorted().toTypedArray(), urls.sorted().toTypedArray())
    }

    private fun loadDivData(): DivData {
        val json = JSONObject(DIV_JSON)
        return DivData(DivParsingEnvironment(ParsingErrorLogger.ASSERT), json)
    }
}

private fun preloadAndCollectUrls(divData: DivData): Set<String> {
    val urls = mutableSetOf<String>()

    val cachingRequestInterceptingCache = object : DivLottieNetworkCache {
        override fun loadCached(url: String): String? = null
        override fun cacheComposition(url: String) {
            urls += url
        }

        override fun cacheComposition(url: String, onComplete: (Throwable?) -> Unit): Boolean {
            cacheComposition(url)
            onComplete(null)
            return true
        }
    }

    val urlsCollectingHandler = DivLottieExtensionHandler(cache = cachingRequestInterceptingCache)

    val divConfiguration = DivConfiguration.Builder(NoOpDivImageLoader())
        .extension(urlsCollectingHandler)
        .build()

    val preloader = DivPreloader(divConfiguration)

    divData.states.forEach { state ->
        preloader.preload(state.div, ExpressionResolver.EMPTY)
    }
    return urls
}

private val DivLoadReferenceStub = LoadReference {}

private class NoOpDivImageLoader @Inject constructor() : DivImageLoader {

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference = DivLoadReferenceStub

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference = DivLoadReferenceStub

    override fun loadImageBytes(imageUrl: String, callback: DivImageDownloadCallback): LoadReference =
        DivLoadReferenceStub
}

private const val DIV_JSON = """
{
  "log_id": "div_card_with_lottie",
  "states": [
    {
      "state_id": 0,
      "div": {
        "type": "container",
        "items": [
          {
            "type": "container",
            "items": [
              {
                "type": "gif",
                "gif_url": "https://none",
                "extensions": [
                  {
                    "id": "lottie",
                    "params": {
                      "lottie_url": "https://assets9.lottiefiles.com/packages/lf20_edpg3c3s.json"
                    }
                  }
                ]
              },
              {
                "type": "gif",
                "gif_url": "https://none",
                "extensions": [
                  {
                    "id": "lottie",
                    "params": {
                      "lottie_url": "https://assets9.lottiefiles.com/packages/lf20_edpg3c3s.json"
                    }
                  }
                ]
              },
              {
                "type": "container",
                "orientation": "horizontal",
                "items": [
                  {
                    "type": "gif",
                    "gif_url": "https://none",
                    "extensions": [
                      {
                        "id": "lottie",
                        "params": {
                          "lottie_url": "https://raw.githubusercontent.com/thesvbd/Lottie-examples/master/assets/animations/menu.json"
                        }
                      }
                    ]
                  },
                  {
                    "type": "gif",
                    "gif_url": "https://none",
                    "extensions": [
                      {
                        "id": "lottie",
                        "params": {
                          "lottie_url": "https://assets9.lottiefiles.com/packages/lf20_qn2snk.json"
                        }
                      }
                    ]
                  }
                ]
              }
            ]
          }
        ]
      }
    }
  ]
}
"""
