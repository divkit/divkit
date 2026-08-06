@file:Suppress("ktlint", "unused")

package divkit.dsl.expression

import divkit.dsl.Color
import divkit.dsl.Url

//region Integer
fun Expression<Boolean>.ifElse(onMatch: Expression<Long>, onMismatch: Int): Expression<Long> =
    ifElse(onMatch, onMismatch.integer())

fun Expression<Boolean>.ifElse(onMatch: Int, onMismatch: Expression<Long>): Expression<Long> =
    ifElse(onMatch.integer(), onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Int, onMismatch: Int): Expression<Long> =
    ifElse(onMatch.integer(), onMismatch.integer())

fun Expression<Boolean>.ifElse(onMatch: Expression<Long>, onMismatch: Long): Expression<Long> =
    ifElse(onMatch, onMismatch.integer())

fun Expression<Boolean>.ifElse(onMatch: Long, onMismatch: Expression<Long>): Expression<Long> =
    ifElse(onMatch.integer(), onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Long, onMismatch: Long): Expression<Long> =
    ifElse(onMatch.integer(), onMismatch.integer())
//endregion

//region Number
fun Expression<Boolean>.ifElse(onMatch: Expression<Double>, onMismatch: Float): Expression<Double> =
    ifElse(onMatch, onMismatch.number())

fun Expression<Boolean>.ifElse(onMatch: Float, onMismatch: Expression<Double>): Expression<Double> =
    ifElse(onMatch.number(), onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Float, onMismatch: Float): Expression<Double> =
    ifElse(onMatch.number(), onMismatch.number())

fun Expression<Boolean>.ifElse(onMatch: Expression<Double>, onMismatch: Double): Expression<Double> =
    ifElse(onMatch, onMismatch.number())

fun Expression<Boolean>.ifElse(onMatch: Double, onMismatch: Expression<Double>): Expression<Double> =
    ifElse(onMatch.number(), onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Double, onMismatch: Double): Expression<Double> =
    ifElse(onMatch.number(), onMismatch.number())
//endregion

//region String
fun Expression<Boolean>.ifElse(onMatch: Expression<String>, onMismatch: String): Expression<String> =
    ifElse(onMatch, onMismatch.string())

fun Expression<Boolean>.ifElse(onMatch: String, onMismatch: Expression<String>): Expression<String> =
    ifElse(onMatch.string(), onMismatch)

fun Expression<Boolean>.ifElse(onMatch: String, onMismatch: String): Expression<String> =
    ifElse(onMatch.string(), onMismatch.string())
//endregion

//region Boolean
fun Expression<Boolean>.ifElse(onMatch: Expression<Boolean>, onMismatch: Boolean): Expression<Boolean> =
    ifElse(onMatch, onMismatch.boolean())

fun Expression<Boolean>.ifElse(onMatch: Boolean, onMismatch: Expression<Boolean>): Expression<Boolean> =
    ifElse(onMatch.boolean(), onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Boolean, onMismatch: Boolean): Expression<Boolean> =
    ifElse(onMatch.boolean(), onMismatch.boolean())
//endregion


//region Color
fun Expression<Boolean>.ifElse(onMatch: Expression<Color>, onMismatch: Color): Expression<Color> =
    ifElse(onMatch, onMismatch.color())

fun Expression<Boolean>.ifElse(onMatch: Color, onMismatch: Expression<Color>): Expression<Color> =
    ifElse(onMatch.color(), onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Color, onMismatch: Color): Expression<Color> =
    ifElse(onMatch.color(), onMismatch.color())
//endregion


//region Url
fun Expression<Boolean>.ifElse(onMatch: Expression<Url>, onMismatch: Url): Expression<Url> =
    ifElse(onMatch, onMismatch.url())

fun Expression<Boolean>.ifElse(onMatch: Url, onMismatch: Expression<Url>): Expression<Url> =
    ifElse(onMatch.url(), onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Url, onMismatch: Url): Expression<Url> =
    ifElse(onMatch.url(), onMismatch.url())
//endregion

//region Cross types
fun Expression<Boolean>.ifElse(onMatch: Int, onMismatch: Long): Expression<Long> =
    ifElse(onMatch.integer(), onMismatch.integer())

fun Expression<Boolean>.ifElse(onMatch: Long, onMismatch: Int): Expression<Long> =
    ifElse(onMatch.integer(), onMismatch.integer())

fun Expression<Boolean>.ifElse(onMatch: Float, onMismatch: Int): Expression<Double> =
    ifElse(onMatch.number(), onMismatch.number())

fun Expression<Boolean>.ifElse(onMatch: Float, onMismatch: Long): Expression<Double> =
    ifElse(onMatch.number(), onMismatch.number())

fun Expression<Boolean>.ifElse(onMatch: Float, onMismatch: Double): Expression<Double> =
    ifElse(onMatch.number(), onMismatch.number())

fun Expression<Boolean>.ifElse(onMatch: Double, onMismatch: Int): Expression<Double> =
    ifElse(onMatch.number(), onMismatch.number())

fun Expression<Boolean>.ifElse(onMatch: Double, onMismatch: Long): Expression<Double> =
    ifElse(onMatch.number(), onMismatch.number())

fun Expression<Boolean>.ifElse(onMatch: Double, onMismatch: Float): Expression<Double> =
    ifElse(onMatch.number(), onMismatch.number())
//endregion
