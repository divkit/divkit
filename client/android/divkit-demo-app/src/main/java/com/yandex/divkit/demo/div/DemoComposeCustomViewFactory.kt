package com.yandex.divkit.demo.div

import android.graphics.drawable.GradientDrawable
import android.os.SystemClock
import android.widget.Chronometer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory

class CustomTextViewFactory(private val text: String) : DivCustomViewFactory {
    @Composable
    override fun Content(environment: DivCustomEnvironment) {
        Box(
            modifier = environment.modifier,
            contentAlignment = Alignment.CenterStart
        ) {
            BasicText(text = text)
        }
    }
}

class ChronometerViewFactory : DivCustomViewFactory {
    @Composable
    override fun Content(environment: DivCustomEnvironment) {
        AndroidView(
            factory = { context ->
                Chronometer(context).apply {
                    setPadding(30, 30, 30, 30)
                    val gd = GradientDrawable()
                    gd.orientation = GradientDrawable.Orientation.BL_TR
                    gd.colors = intArrayOf(
                        -0xFF0000, -0xFF7F00, -0xF00F00,
                        -0x00FF00, -0x0000FF, -0x2E2B5F, -0x8B00FF,
                    )
                    background = gd
                    textSize = 20f
                }
            },
            modifier = environment.modifier,
            update = { view ->
                view.apply {
                    base = SystemClock.elapsedRealtime()
                    start()
                }
            },
        )
    }
}

class CustomContainerViewFactory : DivCustomViewFactory {
    @Composable
    override fun Content(environment: DivCustomEnvironment) {
        Column(modifier = environment.modifier) { environment.items() }
    }
}

class NestedScrollViewFactory : DivCustomViewFactory {
    @Composable
    override fun Content(environment: DivCustomEnvironment) {
        Column(modifier = environment.modifier.verticalScroll(rememberScrollState())) {
            environment.items()
        }
    }
}
