package com.yandex.divkit.multiplaform.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yandex.div.picasso.PicassoDivImageLoader
import com.yandex.divkit.multiplatform.DivKitEnvironment
import com.yandex.divkit.multiplatform.inject

class MainActivity : ComponentActivity() {

    private lateinit var environment: DivKitEnvironment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        environment = DivKitEnvironment.Builder(baseContext = this)
            .imageLoaderFactory { ctx -> PicassoDivImageLoader(ctx) }
            .lifecycleOwner(this)
            .build()

        setContent {
            inject(environment) {
                MainScreen()
            }
        }
    }
}
