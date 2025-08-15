package com.yandex.divkit.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.markdown.DivMarkdownExtensionHandler
import com.yandex.div.picasso.PicassoDivImageLoader
import com.yandex.div.rive.OkHttpDivRiveNetworkDelegate
import com.yandex.div.rive.RiveCustomViewAdapter
import com.yandex.div.zoom.DivPinchToZoomConfiguration
import com.yandex.div.zoom.DivPinchToZoomExtensionHandler
import com.yandex.divkit.sample.databinding.ActivityMainPageBinding
import okhttp3.OkHttpClient

class MainPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding
    private val assetReader = AssetReader(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title

        val divJson = assetReader.read("sample.json")
        val templatesJson = divJson.optJSONObject("templates")
        val cardJson = divJson.getJSONObject("card")

        val divContext = Div2Context(
            baseContext = this,
            configuration = createDivConfiguration(),
            lifecycleOwner = this
        )

        val divView = Div2ViewFactory(divContext, templatesJson).createView(cardJson)
        binding.list.addView(divView)
    }

    private fun createDivConfiguration(): DivConfiguration {
        return DivConfiguration.Builder(PicassoDivImageLoader(this))
            .actionHandler(SampleDivActionHandler())
            .extension(
                DivPinchToZoomExtensionHandler(
                    DivPinchToZoomConfiguration.Builder(this).build()
                )
            )
            .extension(DivMarkdownExtensionHandler(this))
            .divCustomContainerViewAdapter(RiveCustomViewAdapter.Builder(this, OkHttpDivRiveNetworkDelegate(OkHttpClient.Builder().build())).build())
            .visualErrorsEnabled(true)
            .apply {
                if (ENABLE_CUSTOM_FONTS) {
                    typefaceProvider(MultiTypefaceProvider(this@MainPageActivity))
                }
            }
            .additionalTypefaceProviders(mapOf(
                "artika" to ArtikaTypefaceProvider(this@MainPageActivity),
                "super_trivia" to SuperTriviaTypefaceProvider(this@MainPageActivity),
            ))
            .build()
    }

    companion object {
        private const val ENABLE_CUSTOM_FONTS = true
    }
}
