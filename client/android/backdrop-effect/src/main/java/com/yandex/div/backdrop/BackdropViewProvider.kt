package com.yandex.div.backdrop

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ancestors
import com.yandex.div.core.view2.Div2View

sealed class BackdropViewProvider {
    abstract val backdropView: View?
}

class ParentBackdropViewProvider(
    view: View
) : BackdropViewProvider() {

    override val backdropView: ViewGroup? by lazy(LazyThreadSafetyMode.NONE) {
        view.ancestors
            .filterIsInstance<ViewGroup>()
            .firstOrNull()
    }
}

class DivViewBackdropViewProvider(
    view: View
) : BackdropViewProvider() {

    override val backdropView: Div2View? by lazy(LazyThreadSafetyMode.NONE) {
        view.ancestors
            .filterIsInstance<Div2View>()
            .firstOrNull()
    }
}

internal class TaggedBackdropViewProvider(
    view: View,
    private val tag: String
) : BackdropViewProvider() {

    override val backdropView: View? by lazy(LazyThreadSafetyMode.NONE) {
        findTaggedAncestor(view) ?: findDivView(view)?.findViewWithTag<View>(tag)
    }

    private fun findTaggedAncestor(view: View): ViewGroup? {
        return view.ancestors
            .filterIsInstance<ViewGroup>()
            .takeWhile { it !is Div2View }
            .find { it.tag == tag }
    }

    private fun findDivView(view: View): Div2View? {
        return view.ancestors
            .filterIsInstance<Div2View>()
            .firstOrNull()
    }
}
