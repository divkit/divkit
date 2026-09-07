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
 * [DivLocalScope], reducing the number of [Expression.observe] subscriptions.
 * A subscription starts when an entry is created and is kept while any pending or remembered
 * reference remains.
 *
 * [Expression.ConstantExpression] never reaches the cache — it is fast-pathed in [ExpressionUtils].
 *
 * Cache access and expression updates are confined to the main thread.
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
            ).also { entry ->
                entry.subscription = expression.observe(expressionResolver) { value ->
                    entry.state.value = value
                }
            }
        } as Entry<T>
        entry.pendingRefCount++
        return ExpressionCacheRef(this, entry)
    }

    /**
     * Promotes a pending reference to the [entry] created and subscribed by [getOrCreate].
     */
    fun <T : Any> retain(entry: Entry<T>) {
        if (entry.pendingRefCount <= 0) {
            return
        }
        entry.pendingRefCount--
        entry.refCount++
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
 * [onRemembered] promotes a pending reference; [onForgotten] releases
 * a remembered reference; [onAbandoned] releases one that never reached the apply phase.
 */
internal class ExpressionCacheRef<T : Any>(
    private val cache: ExpressionCache,
    private val entry: ExpressionCache.Entry<T>
) : RememberObserver {

    val value: T get() = entry.state.value

    override fun onRemembered() {
        cache.retain(entry)
    }

    override fun onForgotten() {
        cache.release(entry)
    }

    override fun onAbandoned() {
        cache.abandon(entry)
    }
}
