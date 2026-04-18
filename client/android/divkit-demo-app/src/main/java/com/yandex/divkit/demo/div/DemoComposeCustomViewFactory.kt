package com.yandex.divkit.demo.div

import android.graphics.drawable.GradientDrawable
import android.os.SystemClock
import android.widget.Chronometer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div2.DivCustom

class DemoComposeCustomViewFactory : DivCustomViewFactory {

    @Composable
    override fun Content(
        environment: DivCustomEnvironment,
        modifier: Modifier,
    ) {
        val div = environment.div
        when (div.customType) {
            "old_custom_card_1" -> ComposeText("hi, i'm old card", modifier)
            "old_custom_card_2" -> ComposeText("and i'm old as well!", modifier)
            "custom_card_with_min_size" -> ComposeText("Text is placed in frame", modifier)
            "new_custom_card_1",
            "new_custom_card_2",
            "android_view_clock" -> ChronometerView(modifier)
            "new_custom_container_1",
            "compose_container" -> Column(modifier = modifier) { environment.items() }
            "nested_scroll_view" -> Column(modifier = modifier.verticalScroll(rememberScrollState())) { environment.items() }
            "compose_hello" -> ComposeHello(div, modifier)
        }
    }
}

@Composable
private fun ComposeHello(div: DivCustom, modifier: Modifier) {
    val title = div.customProps?.optString("title") ?: "Hello from Compose!"
    val subtitle = div.customProps?.optString("subtitle")
    val bgColorHex = div.customProps?.optString("background_color")
    val bgColor = bgColorHex.parseColor() ?: Color(0xFF6200EE)
    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column {
            BasicText(
                text = title,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            if (!subtitle.isNullOrEmpty()) {
                BasicText(
                    text = subtitle,
                    style = TextStyle(
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                    ),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun ComposeText(text: String, modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        BasicText(text = text)
    }
}

@Composable
private fun ChronometerView(modifier: Modifier) {
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
        modifier = modifier,
        update = { view ->
            view.apply {
                base = SystemClock.elapsedRealtime()
                start()
            }
        },
    )
}

private fun String?.parseColor(): Color? {
    if (isNullOrEmpty()) return null
    return runCatching { Color(this.toColorInt()) }.getOrNull()
}
