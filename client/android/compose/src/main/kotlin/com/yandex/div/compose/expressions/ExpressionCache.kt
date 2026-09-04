package com.yandex.div.compose.expressions

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.mutableStateOf
import com.yandex.div.compose.dagger.DivLocalScope
import com.yandex.div.core.Disposable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Shares [MutableState] instances for the same expression string and value type within a
 * [DivLocalScope], reducing the number of [Expression.observe] subscriptions created during the
 * apply-changes phase of composition.
 *
 * [Expression.ConstantExpression] never reaches the cache — it is fast-pathed in [ExpressionUtils].
 *
 * All Compose apply-changes callbacks run on the main thread; no synchronisation is required.
 */
@DivLocalScope
internal class ExpressionCache @Inject constructor(
    private val expressionResolver: ExpressionResolver
) {

    data class Key(
        val rawExpression: String,
        val valueType: KClass<*>
    )

    class Entry<T : Any>(
        val key: Key,
        initialValue: T
    ) {
        val state: MutableState<T> = mutableStateOf(initialValue)
        var subscription: Disposable? = null
        var pendingRefCount: Int = 0
        var refCount: Int = 0
    }

    private val entries = HashMap<Key, Entry<*>>()

    inline fun <reified T : Any> getOrCreate(expression: Expression<T>): ExpressionCacheRef<T> {
        return getOrCreate(expression, T::class)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrCreate(
        expression: Expression<T>,
        valueType: KClass<T>
    ): ExpressionCacheRef<T> {
        val key = Key(expression.rawValue.toString(), valueType)
        val entry = entries.getOrPut(key) {
            Entry(
                key = key,
                initialValue = expression.evaluate(expressionResolver)
            )
        } as Entry<T>
        entry.pendingRefCount++
        return ExpressionCacheRef(this, entry, expression)
    }

    /**
     * Increments the ref count for the [entry] already committed by [getOrCreate], and sets up
     * the [Expression.observe] subscription on first reference.
     *
     * [entry] is already the canonical entry from the cache map (assigned in [getOrCreate]),
     * so no reconciliation is needed here.
     */
    fun <T : Any> retain(expression: Expression<T>, entry: Entry<T>) {
        if (entry.pendingRefCount <= 0) {
            return
        }
        entry.pendingRefCount--
        entry.refCount++
        if (entry.subscription == null) {
            entry.subscription = expression.observe(expressionResolver) { value ->
                entry.state.value = value
            }
        }
    }

    fun <T : Any> release(entry: Entry<T>) {
        if (entry.refCount <= 0) {
            return
        }
        entry.refCount--
        removeIfUnused(entry)
    }

    fun <T : Any> abandon(entry: Entry<T>) {
        if (entry.pendingRefCount <= 0) {
            return
        }
        entry.pendingRefCount--
        removeIfUnused(entry)
    }

    private fun <T : Any> removeIfUnused(entry: Entry<T>) {
        if (entry.pendingRefCount == 0 && entry.refCount == 0) {
            entry.subscription?.close()
            entry.subscription = null
            entries.remove(entry.key)
        }
    }
}

/**
 * A [RememberObserver] that manages one reference in [ExpressionCache].
 *
 * [onRemembered] promotes a pending reference and sets up the subscription; [onForgotten] releases
 * a remembered reference; [onAbandoned] releases one that never reached the apply phase.
 */
internal class ExpressionCacheRef<T : Any>(
    private val cache: ExpressionCache,
    private val entry: ExpressionCache.Entry<T>,
    private val expression: Expression<T>
) : RememberObserver {

    val value: T get() = entry.state.value

    override fun onRemembered() {
        cache.retain(expression, entry)
    }

    override fun onForgotten() {
        cache.release(entry)
    }

    override fun onAbandoned() {
        cache.abandon(entry)
    }
}
