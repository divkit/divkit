package com.yandex.divkit.demo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.neovisionaries.ws.client.WebSocketFactory
import com.yandex.div.core.DivKit
import com.yandex.div.core.histogram.HistogramBridge
import com.yandex.div.histogram.HistogramFilter
import com.yandex.div.histogram.RenderConfiguration
import com.yandex.div.video.custom.VideoCache
import com.yandex.div.video.custom.VideoCustomViewController
import com.yandex.divkit.demo.div.DemoDivImageLoader
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
internal object Container {
    lateinit var context: Context
        private set

    val preferences by lazy {
        Preferences(context)
    }

    val httpClient = OkHttpClient.Builder().addNetworkInterceptor(StethoInterceptor()).build()

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

    val imageLoader by lazy { DemoDivImageLoader(context) }

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

    fun initialize(context: Context) {
        this.context = context
    }
}
