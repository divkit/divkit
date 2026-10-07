package com.yandex.div.compose.lottie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieDynamicProperties
import com.yandex.div.compose.extensions.DivExtensionEnvironment

/**
 * Creates dynamic properties for a loaded Lottie composition.
 *
 * The method is called from composition, so implementations can use Compose state and Lottie's
 * `rememberLottieDynamicProperty` and `rememberLottieDynamicProperties` helpers:
 *
 * ```
 * val provider = object : LottieDynamicPropertiesProvider {
 *     @Composable
 *     override fun getDynamicProperties(
 *         context: LottieDynamicPropertiesContext,
 *     ): LottieDynamicProperties {
 *         val color = rememberLottieDynamicProperty(
 *             LottieProperty.COLOR,
 *             Color.RED,
 *             "Shape Layer",
 *             "Fill",
 *         )
 *         return rememberLottieDynamicProperties(color)
 *     }
 * }
 * ```
 */
interface LottieDynamicPropertiesProvider {
    /** Returns dynamic properties for the current [context], or `null` to use the animation unchanged. */
    @Composable
    fun getDynamicProperties(context: LottieDynamicPropertiesContext): LottieDynamicProperties?
}

/** Context available while creating dynamic properties for a loaded Lottie composition. */
@Stable
class LottieDynamicPropertiesContext internal constructor(
    val environment: DivExtensionEnvironment,
    val composition: LottieComposition,
)
