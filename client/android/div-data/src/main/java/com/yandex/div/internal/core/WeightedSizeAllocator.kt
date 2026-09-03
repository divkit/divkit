package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import kotlin.math.ceil

@InternalApi
fun resolveWeightedSizes(
    weights: FloatArray,
    baseSizes: IntArray,
    minimumSize: Int,
): IntArray {
    require(weights.size == baseSizes.size)

    var totalFixedSize = 0
    var totalWeight = 0f
    var maxWeightedSize = 0f
    weights.indices.forEach { index ->
        val weight = weights[index]
        if (weight > 0f) {
            totalWeight += weight
            maxWeightedSize = maxOf(maxWeightedSize, baseSizes[index] / weight)
        } else {
            totalFixedSize += baseSizes[index]
        }
    }
    if (totalWeight <= 0f) return baseSizes.copyOf()

    var contentSize = totalFixedSize
    weights.indices.forEach { index ->
        val weight = weights[index]
        if (weight > 0f) {
            contentSize += ceil(weight * maxWeightedSize).toInt()
        }
    }

    val resolvedSize = maxOf(minimumSize, contentSize)
    val weightedSize = maxOf(0, resolvedSize - totalFixedSize) / totalWeight
    return IntArray(weights.size) { index ->
        val weight = weights[index]
        if (weight > 0f) ceil(weight * weightedSize).toInt() else baseSizes[index]
    }
}
