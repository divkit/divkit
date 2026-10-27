package com.yandex.div.compose.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div2.DivAnimator
import com.yandex.div2.DivBase

private val LocalAnimatorOwners = staticCompositionLocalOf<List<AnimatorOwner>> {
    emptyList()
}

@Composable
internal fun WithAnimatorTarget(data: DivBase, content: @Composable () -> Unit) {
    val parentOwners = LocalAnimatorOwners.current
    val animators = data.animators.orEmpty()
    val registersTarget = data.id != null && (animators.isNotEmpty() || parentOwners.isNotEmpty())
    if (!registersTarget && animators.isEmpty()) {
        content()
        return
    }

    val localComponent = LocalComponent.current
    val owners = if (animators.isEmpty()) {
        parentOwners
    } else {
        remember(data, localComponent, parentOwners) {
            listOf(AnimatorOwner(animators, localComponent)) + parentOwners
        }
    }
    if (registersTarget) {
        val storage = LocalDivViewContext.current.component.animatorStorage
        val token = remember { Any() }

        SideEffect {
            storage.registerTarget(token, data.id, owners, localComponent)
        }
        DisposableEffect(storage, token) {
            onDispose { storage.unregisterTarget(token) }
        }
    }

    if (animators.isEmpty()) {
        content()
    } else {
        CompositionLocalProvider(LocalAnimatorOwners provides owners, content = content)
    }
}

internal data class AnimatorOwner(
    val animators: List<DivAnimator>,
    val localComponent: DivLocalComponent,
) {
    fun findAnimator(id: String): DivAnimator? {
        val matches = animators.filter { it.value().id == id }
        if (matches.size > 1) {
            localComponent.reporter.reportError("Animator with id '$id' is ambiguous")
        }
        return matches.singleOrNull()
    }
}
