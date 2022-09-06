package com.yandex.div.core.resources

import android.content.res.Resources
import android.util.TypedValue
import java.util.concurrent.ConcurrentHashMap

/**
 * Resources wrapper that caches primitive values (dimensions/integers/booleans).
 */
internal class PrimitiveResourceCache(baseResources: Resources) : ResourcesWrapper(baseResources) {
    private val booleans = ConcurrentHashMap<Int, Boolean>()
    private val dimensions = ConcurrentHashMap<Int, Float>()
    private val dimensionPixelOffsets = ConcurrentHashMap<Int, Int>()
    private val dimensionPixelSizes = ConcurrentHashMap<Int, Int>()
    private val integers = ConcurrentHashMap<Int, Int>()

    // Optimization to minimize TypedValue allocations (taken from Resources implementation).
    private var tmpValue: TypedValue? = TypedValue()
    private val tmpValueLock = Object()

    @Throws(NotFoundException::class)
    override fun getBoolean(id: Int): Boolean =
        getAndCacheIfPossible(id, booleans, { value ->
            value.type >= TypedValue.TYPE_FIRST_INT && value.type <= TypedValue.TYPE_LAST_INT
        }) { value ->
            value.data != 0
        }

    @Throws(NotFoundException::class)
    override fun getDimension(id: Int): Float =
        getAndCacheIfPossible(id, dimensions, { value ->
            value.type == TypedValue.TYPE_DIMENSION
        }) { value ->
            TypedValue.complexToDimension(value.data, displayMetrics)
        }

    @Throws(NotFoundException::class)
    override fun getDimensionPixelOffset(id: Int): Int =
        getAndCacheIfPossible(id, dimensionPixelOffsets, { value ->
            value.type == TypedValue.TYPE_DIMENSION
        }) { value ->
            TypedValue.complexToDimensionPixelOffset(value.data, displayMetrics)
        }

    @Throws(NotFoundException::class)
    override fun getDimensionPixelSize(id: Int): Int =
        getAndCacheIfPossible(id, dimensionPixelSizes, { value ->
            value.type == TypedValue.TYPE_DIMENSION
        }) { value ->
            TypedValue.complexToDimensionPixelSize(value.data, displayMetrics)
        }

    @Throws(NotFoundException::class)
    override fun getInteger(id: Int): Int =
        getAndCacheIfPossible(id, integers, { value ->
            value.type >= TypedValue.TYPE_FIRST_INT && value.type <= TypedValue.TYPE_LAST_INT
        }) { value ->
            value.data
        }

    @Throws(NotFoundException::class)
    private inline fun <T : Any> getAndCacheIfPossible(id: Int, cache: ConcurrentHashMap<Int, T>,
                                                       validate: (TypedValue) -> Boolean,
                                                       transform: (TypedValue) -> T): T {
        cache[id]?.let { return it }
        val value = obtainTempTypedValue()
        try {
            super.getValue(id, value, true)
            if (validate(value)) {
                val result = transform(value)
                // Store resource value in cache only if it cannot be mutated by configuration
                // changes.
                if (value.changingConfigurations == 0) {
                    cache.putIfAbsent(id, result)
                }
                return result
            }
            throw NotFoundException("Resource ID #0x" + Integer.toHexString(id)
                + " type #0x" + Integer.toHexString(value.type) + " is not valid")
        } finally {
            releaseTempTypedValue(value)
        }
    }

    private fun obtainTempTypedValue(): TypedValue {
        var value: TypedValue? = null
        synchronized(tmpValueLock) {
            if (tmpValue != null) {
                value = tmpValue
                tmpValue = null
            }
        }
        return value ?: TypedValue()
    }

    private fun releaseTempTypedValue(value: TypedValue) {
        synchronized(tmpValueLock) {
            if (tmpValue == null) {
                tmpValue = value
            }
        }
    }
}
