package com.yandex.div.compose.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.di.LocalComponent
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

    val component = LocalComponent.current
    val owners = if (animators.isEmpty()) {
        parentOwners
    } else {
        remember(animators, parentOwners) {
            listOf(
                AnimatorOwner(animators = animators, reporter = component.reporter)
            ) + parentOwners
        }
    }
    if (registersTarget) {
        val animatorStorage = component.animatorStorage
        val token = remember { Any() }
        SideEffect {
            animatorStorage.registerTarget(token, data.id, owners, component)
        }
        DisposableEffect(animatorStorage, token) {
            onDispose { animatorStorage.unregisterTarget(token) }
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
    private val reporter: DivReporter,
) {
    fun findAnimator(id: String): DivAnimator? {
        val matches = animators.filter { it.value().id == id }
        if (matches.size > 1) {
            reporter.reportError("Animator with id '$id' is ambiguous")
        }
        return matches.singleOrNull()
    }
}
