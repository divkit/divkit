package com.yandex.divkit.demo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.neovisionaries.ws.client.WebSocketFactory
import com.yandex.div.core.DivKit
import com.yandex.div.glide.GlideDivImageLoader
import com.yandex.div.histogram.HistogramBridge
import com.yandex.div.histogram.HistogramFilter
import com.yandex.div.histogram.RenderConfiguration
import com.yandex.div.picasso.PicassoDivImageLoader
import com.yandex.div.video.custom.VideoCache
import com.yandex.div.video.custom.VideoCustomViewController
import com.yandex.divkit.demo.div.editor.NaiveSSLContext
import com.yandex.divkit.demo.div.histogram.DemoHistogramConfiguration
import com.yandex.divkit.demo.div.histogram.LoggingHistogramBridge
import com.yandex.divkit.demo.div.video.DemoVideoCustomImageCache
import com.yandex.divkit.demo.regression.RegressionDiv2ViewCreator
import com.yandex.divkit.demo.settings.FlagPreferenceProvider
import com.yandex.divkit.demo.settings.Preferences
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import com.yandex.divkit.demo.utils.connectivityManager
import com.yandex.divkit.regression.di.DaggerRegressionComponent
import kotlinx.coroutines.MainScope
import okhttp3.OkHttpClient
import okhttp3.internal.http2.Header
import okhttp3.internal.toHeaderList
import okhttp3.internal.toHeaders
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
internal object Container {
    lateinit var context: Context
        private set

    val preferences by lazy {
        Preferences(context)
    }

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
        .addNetworkInterceptor(StethoInterceptor())

    val httpClient = httpClientBuilder.build()

    val applicationCoroutineScope = MainScope()

    val webSocketFactory: WebSocketFactory by lazy {
        WebSocketFactory()
            .setSSLContext(NaiveSSLContext.getInstance("TLS"))
            .setConnectionTimeout(TimeUnit.SECONDS.toMillis(15).toInt())
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.connectivityManager.defaultProxy?.let { proxyInfo ->
                        proxySettings.host = proxyInfo.host
                        proxySettings.port = proxyInfo.port
                    }
                }
            }
    }

    val imageLoader by lazy {
        if (preferences.imageLoader) {
            PicassoDivImageLoader(context, httpClientBuilder)
        } else {
            GlideDivImageLoader(context)
        }
    }

    val uriHandler by lazy { DivkitDemoUriHandler(context) }

    val videoCustomViewController by lazy {
        val imageCache = DemoVideoCustomImageCache(context)
        val cache = VideoCache(
            imageLoader,
            { imageCache },
            context
        )

        VideoCustomViewController(cache, applicationCoroutineScope, context)
    }

    val flagPreferenceProvider by lazy { FlagPreferenceProvider(context) }

    val regressionComponent by lazy {
        DaggerRegressionComponent.factory().create(
            context = context,
            viewCreator = RegressionDiv2ViewCreator(context)
        )
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
