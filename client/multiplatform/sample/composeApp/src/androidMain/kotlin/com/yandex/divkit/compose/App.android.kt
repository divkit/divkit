package com.yandex.divkit.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yandex.div.multiplatform.DivKitAndroidEnvironment
import com.yandex.div.multiplatform.DivKitEnvironment
import com.yandex.div.picasso.PicassoDivImageLoader

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DivKitAndroidEnvironment.set(
            DivKitEnvironment(
                imageLoaderFactory = { ctx ->
                    PicassoDivImageLoader(ctx)
                },
                lifecycleOwner = null
            )
        )
        setContent { MainScreen() }
    }
}
