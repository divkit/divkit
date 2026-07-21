package com.yandex.divkit.demo.screenshot

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.core.annotations.ExperimentalApi

@OptIn(ExperimentalApi::class)
class TestSolidBackgroundComposeExtensionHandler : DivExtensionHandler {

    @Composable
    override fun Content(
        modifier: Modifier,
        environment: DivExtensionEnvironment,
        content: @Composable (modifier: Modifier) -> Unit
    ) {
        content(modifier.background(Color.Red))
    }
}
