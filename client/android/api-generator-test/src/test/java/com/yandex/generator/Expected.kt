package com.yandex.generator

import android.graphics.Color
import android.net.Uri
import com.yandex.div.json.expressions.ConstantExpressionsList
import com.yandex.div.json.expressions.Expression
import com.yandex.testing.Entity
import com.yandex.testing.EntityWithArray
import com.yandex.testing.EntityWithArrayOfNestedItems
import com.yandex.testing.EntityWithArrayWithTransform
import com.yandex.testing.EntityWithComplexProperty
import com.yandex.testing.EntityWithOptionalComplexProperty
import com.yandex.testing.EntityWithOptionalProperty
import com.yandex.testing.EntityWithOptionalStringEnumProperty
import com.yandex.testing.EntityWithRequiredProperty
import com.yandex.testing.EntityWithStrictArray
import com.yandex.testing.EntityWithStringEnumProperty

internal val ENTITY_WITH_STRING_ENUM_PROPERTY = Entity.WithStringEnumProperty(
    EntityWithStringEnumProperty(Expression.constant(EntityWithStringEnumProperty.Property.SECOND))
)

internal val ENTITY_WITH_OPTIONAL_STRING_ENUM_PROPERTY = Entity.WithOptionalStringEnumProperty(
    EntityWithOptionalStringEnumProperty(Expression.constant(EntityWithOptionalStringEnumProperty.Property.SECOND))
)
internal val ENTITY_WITH_MISSING_STRING_ENUM_PROPERTY = Entity.WithOptionalStringEnumProperty(
    EntityWithOptionalStringEnumProperty(null)
)

internal val ENTITY_WITH_REQUIRED_PROPERTY = Entity.WithRequiredProperty(
    EntityWithRequiredProperty(Expression.constant("Some text"))
)

internal val ENTITY_WITH_OPTIONAL_PROPERTY = Entity.WithOptionalProperty(
    EntityWithOptionalProperty(Expression.constant("Some text"))
)

internal val ENTITY_WITH_MISSING_OPTIONAL_PROPERTY = Entity.WithOptionalProperty(
    EntityWithOptionalProperty(null)
)

internal val ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY = Entity.WithComplexProperty(
    EntityWithComplexProperty(EntityWithComplexProperty.Property(Expression.constant(Uri.parse("https://ya.ru"))))
)

internal val ENTITY_WITH_OPTIONAL_COMPLEX_PROPERTY = Entity.WithOptionalComplexProperty(
    EntityWithOptionalComplexProperty(EntityWithOptionalComplexProperty.Property(Expression.constant(Uri.parse("https://ya.ru"))))
)

internal val ENTITY_WITH_MISSING_OPTIONAL_COMPLEX_PROPERTY = Entity.WithOptionalComplexProperty(
    EntityWithOptionalComplexProperty(null)
)

internal val ENTITY_WITH_ARRAY = Entity.WithArray(
    EntityWithArray(listOf(
        ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY,
        ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY
    ))
)

internal val ENTITY_WITH_HETEROGENOUS_ARRAY = Entity.WithArray(
    EntityWithArray(listOf(
        ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY,
        ENTITY_WITH_REQUIRED_PROPERTY,
        ENTITY_WITH_STRING_ENUM_PROPERTY
    ))
)

internal val ENTITY_WITH_NESTED_ARRAY = Entity.WithArray(
    EntityWithArray(listOf(ENTITY_WITH_ARRAY))
)

internal val ENTITY_WITH_ARRAY_WITH_TRANSFORM = Entity.WithArrayWithTransform(
    EntityWithArrayWithTransform(ConstantExpressionsList(listOf(
        Color.parseColor("#FF00FF00"),
        Color.parseColor("#AAFF0000")
    )))
)

internal val ENTITY_WITH_ARRAY_OF_NESTED_ITEMS = Entity.WithArrayOfNestedItems(
    EntityWithArrayOfNestedItems(listOf(
        EntityWithArrayOfNestedItems.Item(ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY, Expression.constant("Some text")),
        EntityWithArrayOfNestedItems.Item(ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY, Expression.constant("Some text"))
    ))
)

internal val ENTITY_WITH_STRICT_ARRAY = Entity.WithStrictArray(
    EntityWithStrictArray(listOf(
        ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY,
        ENTITY_WITH_REQUIRED_COMPLEX_PROPERTY
    ))
)
