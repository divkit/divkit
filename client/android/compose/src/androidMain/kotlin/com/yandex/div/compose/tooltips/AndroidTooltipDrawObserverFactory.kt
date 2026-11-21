package com.yandex.div.compose.tooltips

import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@DivContextScope
@Inject
internal class AndroidTooltipDrawObserverFactory : TooltipDrawObserverFactory {

    @Composable
    override fun rememberObserver(): TooltipDrawObserver {
        val view = LocalView.current
        return remember(view) { AndroidTooltipDrawObserver(view) }
    }
}

private class AndroidTooltipDrawObserver(private val view: View) : TooltipDrawObserver {

    override fun observe(onDraw: () -> Unit): () -> Unit {
        val observer = view.viewTreeObserver
        val listener = ViewTreeObserver.OnDrawListener { onDraw() }
        observer.addOnDrawListener(listener)
        return {
            if (observer.isAlive) observer.removeOnDrawListener(listener)
        }
    }
}
