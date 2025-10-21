@file:Suppress("ktlint", "unused")

package divkit.dsl.expression

//region Generic operations
infix fun <T> Expression<T>.equalTo(other: Expression<T>): Expression<Boolean> {
    return CompareExpression(this, other, CompareExpression.CompareOperation.EQUAL)
}

infix fun <T> Expression<T>.notEqualTo(other: Expression<T>): Expression<Boolean> {
    return CompareExpression(this, other, CompareExpression.CompareOperation.NOT_EQUAL)
}

infix fun <T> Expression<T>.moreThan(other: Expression<T>): Expression<Boolean> {
    return CompareExpression(this, other, CompareExpression.CompareOperation.MORE)
}

infix fun <T> Expression<T>.moreThanOrEqualTo(other: Expression<T>): Expression<Boolean> {
    return CompareExpression(this, other, CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

infix fun <T> Expression<T>.lessThan(other: Expression<T>): Expression<Boolean> {
    return CompareExpression(this, other, CompareExpression.CompareOperation.LESS)
}

infix fun <T> Expression<T>.lessThanOrEqualTo(other: Expression<T>): Expression<Boolean> {
    return CompareExpression(this, other, CompareExpression.CompareOperation.LESS_OR_EQUAL)
}
//endregion

//region Equal to
@JvmName("equalToIntExpLong")
infix fun Int.equalTo(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToLongExpLong")
infix fun Long.equalTo(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToIntExpDouble")
infix fun Int.equalTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToLongExpDouble")
infix fun Long.equalTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToFloatExpDouble")
infix fun Float.equalTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToDoubleExpDouble")
infix fun Double.equalTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToStringExpString")
infix fun String.equalTo(other: Expression<String>): Expression<Boolean> {
    return CompareExpression(this.string(), other, CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToBooleanExpBoolean")
infix fun Boolean.equalTo(other: Expression<Boolean>): Expression<Boolean> {
    return CompareExpression(this.boolean(), other, CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToExpLongInt")
infix fun Expression<Long>.equalTo(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToExpLongLong")
infix fun Expression<Long>.equalTo(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToExpDoubleDouble")
infix fun Expression<Double>.equalTo(other: Double): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToExpDoubleFloat")
infix fun Expression<Double>.equalTo(other: Float): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToExpDoubleLong")
infix fun Expression<Double>.equalTo(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToExpDoubleInt")
infix fun Expression<Double>.equalTo(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToExpStringString")
infix fun Expression<String>.equalTo(other: String): Expression<Boolean> {
    return CompareExpression(this, other.string(), CompareExpression.CompareOperation.EQUAL)
}

@JvmName("equalToExpBooleanBoolean")
infix fun Expression<Boolean>.equalTo(other: Boolean): Expression<Boolean> {
    return CompareExpression(this, other.boolean(), CompareExpression.CompareOperation.EQUAL)
}
//endregion

//region Not equal to
@JvmName("notEqualToIntExpLong")
infix fun Int.notEqualTo(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToLongExpLong")
infix fun Long.notEqualTo(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToIntExpDouble")
infix fun Int.notEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToLongExpDouble")
infix fun Long.notEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToFloatExpDouble")
infix fun Float.notEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToDoubleExpDouble")
infix fun Double.notEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToStringExpString")
infix fun String.notEqualTo(other: Expression<String>): Expression<Boolean> {
    return CompareExpression(this.string(), other, CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToBooleanExpBoolean")
infix fun Boolean.notEqualTo(other: Expression<Boolean>): Expression<Boolean> {
    return CompareExpression(this.boolean(), other, CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToExpLongInt")
infix fun Expression<Long>.notEqualTo(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToExpLongLong")
infix fun Expression<Long>.notEqualTo(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToExpDoubleDouble")
infix fun Expression<Double>.notEqualTo(other: Double): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToExpDoubleFloat")
infix fun Expression<Double>.notEqualTo(other: Float): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToExpDoubleLong")
infix fun Expression<Double>.notEqualTo(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToExpDoubleInt")
infix fun Expression<Double>.notEqualTo(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToExpStringString")
infix fun Expression<String>.notEqualTo(other: String): Expression<Boolean> {
    return CompareExpression(this, other.string(), CompareExpression.CompareOperation.NOT_EQUAL)
}

@JvmName("notEqualToExpBooleanBoolean")
infix fun Expression<Boolean>.notEqualTo(other: Boolean): Expression<Boolean> {
    return CompareExpression(this, other.boolean(), CompareExpression.CompareOperation.NOT_EQUAL)
}
//endregion

//region More than
@JvmName("moreThanIntExpLong")
infix fun Int.moreThan(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.MORE)
}

@JvmName("moreThanLongExpLong")
infix fun Long.moreThan(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.MORE)
}

@JvmName("moreThanIntExpDouble")
infix fun Int.moreThan(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.MORE)
}

@JvmName("moreThanLongExpDouble")
infix fun Long.moreThan(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.MORE)
}

@JvmName("moreThanFloatExpDouble")
infix fun Float.moreThan(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.MORE)
}

@JvmName("moreThanDoubleExpDouble")
infix fun Double.moreThan(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.MORE)
}

@JvmName("moreThanExpLongInt")
infix fun Expression<Long>.moreThan(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.MORE)
}

@JvmName("moreThanExpLongLong")
infix fun Expression<Long>.moreThan(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.MORE)
}

@JvmName("moreThanExpDoubleDouble")
infix fun Expression<Double>.moreThan(other: Double): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.MORE)
}

@JvmName("moreThanExpDoubleFloat")
infix fun Expression<Double>.moreThan(other: Float): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.MORE)
}

@JvmName("moreThanExpDoubleLong")
infix fun Expression<Double>.moreThan(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.MORE)
}

@JvmName("moreThanExpDoubleInt")
infix fun Expression<Double>.moreThan(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.MORE)
}
//endregion

//region More than or equal to
@JvmName("moreThanOrEqualToIntExpLong")
infix fun Int.moreThanOrEqualTo(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

@JvmName("moreThanOrEqualToLongExpLong")
infix fun Long.moreThanOrEqualTo(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

@JvmName("moreThanOrEqualToIntExpDouble")
infix fun Int.moreThanOrEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

@JvmName("moreThanOrEqualToLongExpDouble")
infix fun Long.moreThanOrEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

@JvmName("moreThanOrEqualToFloatExpDouble")
infix fun Float.moreThanOrEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

@JvmName("moreThanOrEqualToDoubleExpDouble")
infix fun Double.moreThanOrEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

@JvmName("moreThanOrEqualToExpLongInt")
infix fun Expression<Long>.moreThanOrEqualTo(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

@JvmName("moreThanOrEqualToExpLongLong")
infix fun Expression<Long>.moreThanOrEqualTo(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

@JvmName("moreThanOrEqualToExpDoubleDouble")
infix fun Expression<Double>.moreThanOrEqualTo(other: Double): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

@JvmName("moreThanOrEqualToExpDoubleFloat")
infix fun Expression<Double>.moreThanOrEqualTo(other: Float): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

@JvmName("moreThanOrEqualToExpDoubleInt")
infix fun Expression<Double>.moreThanOrEqualTo(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.MORE_OR_EQUAL)
}

@JvmName("moreThanOrEqualToExpDoubleLong")
infix fun Expression<Double>.moreThanOrEqualTo(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.MORE_OR_EQUAL)
}
//endregion

//region Less than
@JvmName("lessThanIntExpLong")
infix fun Int.lessThan(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.LESS)
}

@JvmName("lessThanLongExpLong")
infix fun Long.lessThan(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.LESS)
}

@JvmName("lessThanIntExpDouble")
infix fun Int.lessThan(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.LESS)
}

@JvmName("lessThanLongExpDouble")
infix fun Long.lessThan(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.LESS)
}

@JvmName("lessThanFloatExpDouble")
infix fun Float.lessThan(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.LESS)
}

@JvmName("lessThanDoubleExpDouble")
infix fun Double.lessThan(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.LESS)
}

@JvmName("lessThanExpLongInt")
infix fun Expression<Long>.lessThan(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.LESS)
}

@JvmName("lessThanExpLongLong")
infix fun Expression<Long>.lessThan(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.LESS)
}

@JvmName("lessThanExpDoubleDouble")
infix fun Expression<Double>.lessThan(other: Double): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.LESS)
}

@JvmName("lessThanExpDoubleFloat")
infix fun Expression<Double>.lessThan(other: Float): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.LESS)
}

@JvmName("lessThanExpDoubleLong")
infix fun Expression<Double>.lessThan(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.LESS)
}

@JvmName("lessThanExpDoubleInt")
infix fun Expression<Double>.lessThan(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.LESS)
}
//endregion

//region Less than or equal to
@JvmName("lessThanOrEqualIntExpLong")
infix fun Int.lessThanOrEqualTo(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.LESS_OR_EQUAL)
}

@JvmName("lessThanOrEqualLongExpLong")
infix fun Long.lessThanOrEqualTo(other: Expression<Long>): Expression<Boolean> {
    return CompareExpression(this.integer(), other, CompareExpression.CompareOperation.LESS_OR_EQUAL)
}

@JvmName("lessThanOrEqualIntExpDouble")
infix fun Int.lessThanOrEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.LESS_OR_EQUAL)
}

@JvmName("lessThanOrEqualLongExpDouble")
infix fun Long.lessThanOrEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.LESS_OR_EQUAL)
}

@JvmName("lessThanOrEqualFloatExpDouble")
infix fun Float.lessThanOrEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.LESS_OR_EQUAL)
}

@JvmName("lessThanOrEqualDoubleExpDouble")
infix fun Double.lessThanOrEqualTo(other: Expression<Double>): Expression<Boolean> {
    return CompareExpression(this.number(), other, CompareExpression.CompareOperation.LESS_OR_EQUAL)
}

@JvmName("lessThanOrEqualExpLongInt")
infix fun Expression<Long>.lessThanOrEqualTo(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.LESS_OR_EQUAL)
}

@JvmName("lessThanOrEqualExpLongLong")
infix fun Expression<Long>.lessThanOrEqualTo(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.integer(), CompareExpression.CompareOperation.LESS_OR_EQUAL)
}

@JvmName("lessThanOrEqualExpDoubleDouble")
infix fun Expression<Double>.lessThanOrEqualTo(other: Double): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.LESS_OR_EQUAL)
}

@JvmName("lessThanOrEqualExpDoubleFloat")
infix fun Expression<Double>.lessThanOrEqualTo(other: Float): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.LESS_OR_EQUAL)
}

@JvmName("lessThanOrEqualExpDoubleLong")
infix fun Expression<Double>.lessThanOrEqualTo(other: Long): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.LESS_OR_EQUAL)
}

@JvmName("lessThanOrEqualExpDoubleInt")
infix fun Expression<Double>.lessThanOrEqualTo(other: Int): Expression<Boolean> {
    return CompareExpression(this, other.number(), CompareExpression.CompareOperation.LESS_OR_EQUAL)
}
//endregion
