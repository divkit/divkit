package com.yandex.div.compose.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.MotionDurationScale
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.context.DivViewCoroutineScopeProvider
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div2.DivActionAnimatorStart
import com.yandex.div2.DivAnimator
import javax.inject.Inject
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@DivViewScope
internal class AnimatorStorage @Inject constructor(
    val animationConfiguration: AnimationConfiguration,
    private val coroutineScopeProvider: DivViewCoroutineScopeProvider,
    private val reporter: DivReporter,
) {
    private val targets = mutableMapOf<String, LinkedHashMap<Any, AnimatorTarget>>()
    private val targetIds = mutableMapOf<Any, String>()
    private val pendingStarts = mutableListOf<PendingStart>()
    private val runner = AnimatorRunner(coroutineScopeProvider)

    private var animationsEnabled = false
    private var pendingStartJob: Job? = null

    fun registerTarget(
        token: Any,
        id: String?,
        owners: List<AnimatorOwner>,
        localComponent: DivLocalComponent,
    ) {
        unregisterTarget(token)
        if (id == null) return

        targets.getOrPut(id, ::linkedMapOf)[token] = AnimatorTarget(owners, localComponent)
        targetIds[token] = id
    }

    fun unregisterTarget(token: Any) {
        val id = targetIds.remove(token) ?: return
        targets[id]?.let { targetsForId ->
            targetsForId.remove(token)
            if (targetsForId.isEmpty()) {
                targets.remove(id)
            }
        }
    }

    fun start(scopeId: String, action: DivActionAnimatorStart) {
        if (coroutineScopeProvider.get() == null) {
            pendingStarts += PendingStart(scopeId, action)
            return
        }
        if (!animationsEnabled) return
        if (targets[scopeId].isNullOrEmpty()) {
            pendingStarts += PendingStart(scopeId, action)
            schedulePendingStarts()
            return
        }

        startAnimation(scopeId, action)
    }

    fun stop(scopeId: String, animatorId: String) {
        pendingStarts.removeAll { it.scopeId == scopeId && it.action.animatorId == animatorId }
        runner.cancel(AnimatorKey(scopeId, animatorId))
    }

    fun updateAnimationConfiguration(animationsEnabled: Boolean) {
        val wasEnabled = this.animationsEnabled
        this.animationsEnabled = animationsEnabled
        if (!animationsEnabled) {
            clearPendingStarts()
            if (wasEnabled) runner.endAll()
            return
        }

        drainPendingStarts()
        schedulePendingStarts()
    }

    fun detach() {
        animationsEnabled = false
        clearPendingStarts()
        runner.cancelAll()
        targets.clear()
        targetIds.clear()
    }

    private fun startAnimation(
        scopeId: String,
        action: DivActionAnimatorStart,
    ) {
        val matches = targets[scopeId]?.values.orEmpty()
        val target = when (matches.size) {
            0 -> {
                reporter.reportError("Scope with id '$scopeId' not found")
                return
            }

            1 -> matches.first()
            else -> {
                reporter.reportError("Scope with id '$scopeId' is ambiguous")
                return
            }
        }
        val owner = target.findOwner(action.animatorId)
        if (owner == null) {
            reporter.reportError("Animator with id '${action.animatorId}' not found")
            return
        }
        val animator = owner.findAnimator(action.animatorId) ?: return
        val key = AnimatorKey(scopeId, action.animatorId)
        runner.cancel(key)

        when (animator) {
            is DivAnimator.Color -> reporter.reportError(
                "Color animator not supported: '${action.animatorId}'"
            )

            is DivAnimator.Number -> buildNumberAnimation(
                target.localComponent,
                animator.value,
                action,
                currentDurationScale(),
                reporter,
            )?.let { animation ->
                runner.start(key, animation)
            }
        }
    }

    private fun drainPendingStarts(reportMissing: Boolean = false) {
        if (coroutineScopeProvider.get() == null) return
        if (!animationsEnabled) return

        val ready = mutableListOf<PendingStart>()
        val iterator = pendingStarts.iterator()
        while (iterator.hasNext()) {
            val pending = iterator.next()
            if (targets[pending.scopeId].isNullOrEmpty()) {
                if (reportMissing) {
                    iterator.remove()
                    reporter.reportError("Scope with id '${pending.scopeId}' not found")
                }
            } else {
                iterator.remove()
                ready += pending
            }
        }
        ready.forEach { startAnimation(it.scopeId, it.action) }
    }

    private fun schedulePendingStarts() {
        if (pendingStarts.isEmpty() || !animationsEnabled) return
        if (pendingStartJob?.isActive == true) return
        val coroutineScope = coroutineScopeProvider.get() ?: return

        pendingStartJob = coroutineScope.launch {
            // Wait until all target-registration side effects from this composition are applied.
            withFrameNanos { }
            drainPendingStarts(reportMissing = true)
        }
    }

    private fun currentDurationScale(): Float {
        return coroutineScopeProvider.get()
            ?.coroutineContext
            ?.get(MotionDurationScale)
            ?.scaleFactor
            ?.coerceAtLeast(0f)
            ?: 1f
    }

    private fun clearPendingStarts() {
        pendingStartJob?.cancel()
        pendingStartJob = null
        pendingStarts.clear()
    }

    private data class AnimatorTarget(
        val owners: List<AnimatorOwner>,
        val localComponent: DivLocalComponent,
    ) {
        fun findOwner(animatorId: String): AnimatorOwner? {
            for (owner in owners) {
                if (owner.animators.any { it.value().id == animatorId }) return owner
            }
            return null
        }
    }

    private data class PendingStart(
        val scopeId: String,
        val action: DivActionAnimatorStart,
    )
}

internal class AnimatorRunner(
    private val coroutineScopeProvider: DivViewCoroutineScopeProvider,
) {
    private val running = mutableMapOf<AnimatorKey, RunningAnimator>()

    fun start(key: AnimatorKey, animation: NumberAnimation) {
        val coroutineScope = coroutineScopeProvider.get() ?: return
        animation.start()
        lateinit var runningAnimator: RunningAnimator
        val job = coroutineScope.launch(start = CoroutineStart.LAZY) {
            animation.run()
            complete(key, runningAnimator)
        }
        runningAnimator = RunningAnimator(animation = animation, job = job)
        running[key] = runningAnimator
        job.start()
    }

    fun cancel(key: AnimatorKey) {
        val animator = running.remove(key) ?: return
        animator.job.cancel()
        animator.animation.cancel()
    }

    fun endAll() {
        val animators = running.values.toList()
        running.clear()
        animators.forEach { animator ->
            animator.job.cancel()
            animator.animation.end()
        }
    }

    fun cancelAll() {
        val animators = running.values.toList()
        running.clear()
        animators.forEach { animator ->
            animator.job.cancel()
            animator.animation.cancel()
        }
    }

    private fun complete(key: AnimatorKey, animator: RunningAnimator) {
        if (running[key] !== animator) return

        running.remove(key)
        animator.animation.complete()
    }

    private data class RunningAnimator(
        val animation: NumberAnimation,
        val job: Job,
    )
}

internal data class AnimatorKey(val scopeId: String, val animatorId: String)

@Composable
internal fun AnimatorStorage.observe() {
    val animationsEnabled = animationConfiguration.isEnabledAsState()
    DisposableEffect(this) {
        updateAnimationConfiguration(animationsEnabled)
        onDispose(::detach)
    }
    SideEffect {
        updateAnimationConfiguration(animationsEnabled)
    }
}
