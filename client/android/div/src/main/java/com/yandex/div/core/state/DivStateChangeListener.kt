package com.yandex.div.core.state

import android.view.ViewGroup
import android.view.animation.Interpolator
import androidx.transition.TransitionManager
import com.yandex.div.core.animation.SpringInterpolator
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.animations.SceneRootWatcher
import javax.inject.Provider

@PublicApi
interface DivStateChangeListener {

    fun onDivAnimatedStateChanged(divView: Div2View)

    companion object {
        @JvmField
        val STUB: DivStateChangeListener = object : DivStateChangeListener {
            override fun onDivAnimatedStateChanged(divView: Div2View) = Unit
        }
    }
}

@PublicApi
class DefaultDivStateChangeListener @JvmOverloads constructor(
    private val rootViewProvider: Provider<ViewGroup?>,
    private val interpolator: Interpolator = SpringInterpolator()
) : DivStateChangeListener  {

    @JvmOverloads
    constructor(
        rootView: ViewGroup,
        interpolator: Interpolator = SpringInterpolator()
    ) : this(Provider { rootView }, interpolator)

    override fun onDivAnimatedStateChanged(divView: Div2View) {
        val rootView = rootViewProvider.get() ?: return
        val transition = DivStateTransition(divView).setInterpolator(interpolator)
        TransitionManager.endTransitions(rootView)
        SceneRootWatcher.watchFor(rootView, transition)
        TransitionManager.beginDelayedTransition(rootView, transition)
    }
}
