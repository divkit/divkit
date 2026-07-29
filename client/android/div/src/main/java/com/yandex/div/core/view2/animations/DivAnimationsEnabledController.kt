package com.yandex.div.core.view2.animations

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.util.isSystemAnimationsEnabled
import com.yandex.div.core.view2.Div2View
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@DivScope
@InternalApi
class DivAnimationsEnabledController @Inject constructor(
    @Named(Names.THEMED_CONTEXT) private val context: Context,
    private val provider: DivAnimationsEnabledProvider,
) {

    fun isEnabled(): Boolean =
        context.isSystemAnimationsEnabled() && provider.isAnimationsEnabled()

    fun observe(divView: Div2View, observer: () -> Unit): Disposable {
        val scope = divView.context.lifecycleOwner?.lifecycleScope ?: MainScope()
        val job = scope.launch {
            provider.animationsEnabled.drop(1).collect { observer() }
        }
        return Disposable { job.cancel() }
    }
}
