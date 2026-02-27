package com.yandex.divkit.benchmark

import android.annotation.SuppressLint
import android.content.Context
import com.yandex.div.core.DivKit
import com.yandex.div.glide.GlideDivImageLoader
import com.yandex.div.histogram.HistogramBridge
import com.yandex.div.histogram.HistogramFilter
import com.yandex.div.histogram.RenderConfiguration
import com.yandex.div.video.custom.VideoCache
import com.yandex.div.video.custom.VideoCustomViewController
import com.yandex.divkit.benchmark.div.DemoDivImageLoaderWrapper
import com.yandex.divkit.benchmark.div.histogram.DemoHistogramConfiguration
import com.yandex.divkit.benchmark.div.histogram.LoggingHistogramBridge
import com.yandex.divkit.benchmark.div.video.DemoVideoCustomImageCache
import com.yandex.divkit.benchmark.utils.NaiveSSLContext
import kotlinx.coroutines.MainScope
import okhttp3.OkHttpClient
import okhttp3.internal.http2.Header
import okhttp3.internal.toHeaderList
import okhttp3.internal.toHeaders
import org.json.JSONObject

@SuppressLint("StaticFieldLeak")
internal object Container {
    lateinit var context: Context
        private set

    private val httpHeaders by lazy {
        JSONObject(BuildConfig.HTTP_HEADERS).asHostToHeadersMap()
    }

    private val httpClientBuilder = OkHttpClient.Builder()
        .sslSocketFactory(
            NaiveSSLContext.getInstance("TLS").socketFactory,
            NaiveSSLContext.NaiveTrustManager()
        )
        .addNetworkInterceptor {
            val request = it.request()
            val host = request.url.host

            val headerList = request.headers.toHeaderList() + httpHeaders[host].orEmpty()

            it.proceed(
                request.newBuilder()
                    .headers(headerList.toHeaders())
                    .build()
            )
        }

    val httpClient = httpClientBuilder.build()

    val applicationCoroutineScope = MainScope()

    val imageLoader by lazy {
        val loader = GlideDivImageLoader(context)
        DemoDivImageLoaderWrapper(loader)
    }

    val videoCustomViewController by lazy {
        val imageCache = DemoVideoCustomImageCache(context)
        val cache = VideoCache(
            imageLoader,
            { imageCache },
            context
        )

        VideoCustomViewController(cache, applicationCoroutineScope, context)
    }

    val histogramConfiguration by lazy {
        val histogramBridge: HistogramBridge by lazy(::LoggingHistogramBridge)
        val renderConfiguration by lazy {
            RenderConfiguration(
                measureFilter = HistogramFilter.ON,
                layoutFilter = HistogramFilter.ON,
                drawFilter = HistogramFilter.ON,
            )
        }
        DemoHistogramConfiguration(
            histogramBridge = { histogramBridge },
            renderConfiguration = { renderConfiguration },
        )
    }

    val parsingHistogramReporter by lazy {
        DivKit.getInstance(context).parsingHistogramReporter
    }

    val histogramReporterDelegate by lazy {
        DivKit.getInstance(context).histogramReporterDelegate
    }

    fun initialize(context: Context) {
        this.context = context
    }
}

private fun JSONObject.asHostToHeadersMap(): Map<String, List<Header>> {
    return keys().asSequence()
        .map { host -> host to getJSONObject(host).parseHeaders() }
        .toMap()
}

private fun JSONObject.parseHeaders(): List<Header> {
    return keys().asSequence()
        .map { name -> Header(name, getString(name)) }
        .toList()
}
