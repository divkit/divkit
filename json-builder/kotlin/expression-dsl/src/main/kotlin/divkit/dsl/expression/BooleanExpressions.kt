@file:Suppress("ktlint", "unused")

package divkit.dsl.expression

infix fun Expression<Boolean>.and(other: Expression<Boolean>): Expression<Boolean> {
    return BooleanExpression(this, other, BooleanExpression.BooleanOperation.AND)
}

infix fun Boolean.and(other: Expression<Boolean>): Expression<Boolean> {
    return BooleanExpression(this.boolean(), other, BooleanExpression.BooleanOperation.AND)
}

infix fun Expression<Boolean>.and(other: Boolean): Expression<Boolean> {
    return BooleanExpression(this, other.boolean(), BooleanExpression.BooleanOperation.AND)
}

infix fun Expression<Boolean>.or(other: Expression<Boolean>): Expression<Boolean> {
    return BooleanExpression(this, other, BooleanExpression.BooleanOperation.OR)
}

infix fun Boolean.or(other: Expression<Boolean>): Expression<Boolean> {
    return BooleanExpression(this.boolean(), other, BooleanExpression.BooleanOperation.OR)
}

infix fun Expression<Boolean>.or(other: Boolean): Expression<Boolean> {
    return BooleanExpression(this, other.boolean(), BooleanExpression.BooleanOperation.OR)
}
