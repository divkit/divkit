package com.yandex.div.compose.context

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.dagger.Names
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import javax.inject.Inject
import javax.inject.Named

/**
 * Provides the coroutine scope of the current DivView composition to cached view components.
 *
 * [attach] registers the scope after composition is applied. Compose cancels it when the view
 * leaves composition or its provider changes.
 * The main scope supplies the coroutine context except for the Job, which is owned by Compose.
 */
@DivViewScope
internal class DivViewCoroutineScopeProvider @Inject constructor(
    @Named(Names.MAIN_COROUTINE_SCOPE)
    private val mainCoroutineScope: CoroutineScope,
) {
    private var scope: CoroutineScope? = null

    /**
     * Returns the coroutine scope of the current DivView composition. Returns null before
     * registration and after disposal.
     *
     * Consumer must call [get] for each operation because returning to composition creates a new
     * scope.
     */
    fun get(): CoroutineScope? = scope

    @Composable
    @SuppressLint("ComposableNaming")
    fun attach() {
        // Replacing data can change the provider without removing DivView from composition.
        // Key the scope by provider so the previous card's coroutines are canceled.
        key(this) {
            val scope = rememberCoroutineScope {
                mainCoroutineScope.coroutineContext.minusKey(Job)
            }
            DisposableEffect(this) {
                this@DivViewCoroutineScopeProvider.scope = scope
                onDispose {
                    this@DivViewCoroutineScopeProvider.scope = null
                }
            }
        }
    }
}
