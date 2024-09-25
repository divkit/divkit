// Generated code. Do not modify.

package com.yandex.div.reference

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.data.*
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import org.json.JSONArray
import org.json.JSONObject

internal class JsonParserComponent {

    val entityJsonEntityParser = lazy { EntityJsonParser.EntityParserImpl(this) }
    val entityJsonTemplateParser = lazy { EntityJsonParser.TemplateParserImpl(this) }
    val entityJsonTemplateResolver = lazy { EntityJsonParser.TemplateResolverImpl(this) }

    val entityWithArrayJsonEntityParser = lazy { EntityWithArrayJsonParser.EntityParserImpl(this) }
    val entityWithArrayJsonTemplateParser = lazy { EntityWithArrayJsonParser.TemplateParserImpl(this) }
    val entityWithArrayJsonTemplateResolver = lazy { EntityWithArrayJsonParser.TemplateResolverImpl(this) }

    val entityWithArrayOfEnumsJsonEntityParser = lazy { EntityWithArrayOfEnumsJsonParser.EntityParserImpl(this) }
    val entityWithArrayOfEnumsJsonTemplateParser = lazy { EntityWithArrayOfEnumsJsonParser.TemplateParserImpl(this) }
    val entityWithArrayOfEnumsJsonTemplateResolver = lazy { EntityWithArrayOfEnumsJsonParser.TemplateResolverImpl(this) }

    val entityWithArrayOfExpressionsJsonEntityParser = lazy { EntityWithArrayOfExpressionsJsonParser.EntityParserImpl(this) }
    val entityWithArrayOfExpressionsJsonTemplateParser = lazy { EntityWithArrayOfExpressionsJsonParser.TemplateParserImpl(this) }
    val entityWithArrayOfExpressionsJsonTemplateResolver = lazy { EntityWithArrayOfExpressionsJsonParser.TemplateResolverImpl(this) }

    val entityWithArrayOfNestedItemsJsonEntityParser = lazy { EntityWithArrayOfNestedItemsJsonParser.EntityParserImpl(this) }
    val entityWithArrayOfNestedItemsJsonTemplateParser = lazy { EntityWithArrayOfNestedItemsJsonParser.TemplateParserImpl(this) }
    val entityWithArrayOfNestedItemsJsonTemplateResolver = lazy { EntityWithArrayOfNestedItemsJsonParser.TemplateResolverImpl(this) }

    val entityWithArrayOfNestedItemsItemJsonEntityParser = lazy { EntityWithArrayOfNestedItemsItemJsonParser.EntityParserImpl(this) }
    val entityWithArrayOfNestedItemsItemJsonTemplateParser = lazy { EntityWithArrayOfNestedItemsItemJsonParser.TemplateParserImpl(this) }
    val entityWithArrayOfNestedItemsItemJsonTemplateResolver = lazy { EntityWithArrayOfNestedItemsItemJsonParser.TemplateResolverImpl(this) }

    val entityWithArrayWithTransformJsonEntityParser = lazy { EntityWithArrayWithTransformJsonParser.EntityParserImpl(this) }
    val entityWithArrayWithTransformJsonTemplateParser = lazy { EntityWithArrayWithTransformJsonParser.TemplateParserImpl(this) }
    val entityWithArrayWithTransformJsonTemplateResolver = lazy { EntityWithArrayWithTransformJsonParser.TemplateResolverImpl(this) }

    val entityWithComplexPropertyJsonEntityParser = lazy { EntityWithComplexPropertyJsonParser.EntityParserImpl(this) }
    val entityWithComplexPropertyJsonTemplateParser = lazy { EntityWithComplexPropertyJsonParser.TemplateParserImpl(this) }
    val entityWithComplexPropertyJsonTemplateResolver = lazy { EntityWithComplexPropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithComplexPropertyPropertyJsonEntityParser = lazy { EntityWithComplexPropertyPropertyJsonParser.EntityParserImpl(this) }
    val entityWithComplexPropertyPropertyJsonTemplateParser = lazy { EntityWithComplexPropertyPropertyJsonParser.TemplateParserImpl(this) }
    val entityWithComplexPropertyPropertyJsonTemplateResolver = lazy { EntityWithComplexPropertyPropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithComplexPropertyWithDefaultValueJsonEntityParser = lazy { EntityWithComplexPropertyWithDefaultValueJsonParser.EntityParserImpl(this) }
    val entityWithComplexPropertyWithDefaultValueJsonTemplateParser = lazy { EntityWithComplexPropertyWithDefaultValueJsonParser.TemplateParserImpl(this) }
    val entityWithComplexPropertyWithDefaultValueJsonTemplateResolver = lazy { EntityWithComplexPropertyWithDefaultValueJsonParser.TemplateResolverImpl(this) }

    val entityWithComplexPropertyWithDefaultValuePropertyJsonEntityParser = lazy { EntityWithComplexPropertyWithDefaultValuePropertyJsonParser.EntityParserImpl(this) }
    val entityWithComplexPropertyWithDefaultValuePropertyJsonTemplateParser = lazy { EntityWithComplexPropertyWithDefaultValuePropertyJsonParser.TemplateParserImpl(this) }
    val entityWithComplexPropertyWithDefaultValuePropertyJsonTemplateResolver = lazy { EntityWithComplexPropertyWithDefaultValuePropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithEntityPropertyJsonEntityParser = lazy { EntityWithEntityPropertyJsonParser.EntityParserImpl(this) }
    val entityWithEntityPropertyJsonTemplateParser = lazy { EntityWithEntityPropertyJsonParser.TemplateParserImpl(this) }
    val entityWithEntityPropertyJsonTemplateResolver = lazy { EntityWithEntityPropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithJsonPropertyJsonEntityParser = lazy { EntityWithJsonPropertyJsonParser.EntityParserImpl(this) }
    val entityWithJsonPropertyJsonTemplateParser = lazy { EntityWithJsonPropertyJsonParser.TemplateParserImpl(this) }
    val entityWithJsonPropertyJsonTemplateResolver = lazy { EntityWithJsonPropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithOptionalComplexPropertyJsonEntityParser = lazy { EntityWithOptionalComplexPropertyJsonParser.EntityParserImpl(this) }
    val entityWithOptionalComplexPropertyJsonTemplateParser = lazy { EntityWithOptionalComplexPropertyJsonParser.TemplateParserImpl(this) }
    val entityWithOptionalComplexPropertyJsonTemplateResolver = lazy { EntityWithOptionalComplexPropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithOptionalComplexPropertyPropertyJsonEntityParser = lazy { EntityWithOptionalComplexPropertyPropertyJsonParser.EntityParserImpl(this) }
    val entityWithOptionalComplexPropertyPropertyJsonTemplateParser = lazy { EntityWithOptionalComplexPropertyPropertyJsonParser.TemplateParserImpl(this) }
    val entityWithOptionalComplexPropertyPropertyJsonTemplateResolver = lazy { EntityWithOptionalComplexPropertyPropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithOptionalPropertyJsonEntityParser = lazy { EntityWithOptionalPropertyJsonParser.EntityParserImpl(this) }
    val entityWithOptionalPropertyJsonTemplateParser = lazy { EntityWithOptionalPropertyJsonParser.TemplateParserImpl(this) }
    val entityWithOptionalPropertyJsonTemplateResolver = lazy { EntityWithOptionalPropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithOptionalStringEnumPropertyJsonEntityParser = lazy { EntityWithOptionalStringEnumPropertyJsonParser.EntityParserImpl(this) }
    val entityWithOptionalStringEnumPropertyJsonTemplateParser = lazy { EntityWithOptionalStringEnumPropertyJsonParser.TemplateParserImpl(this) }
    val entityWithOptionalStringEnumPropertyJsonTemplateResolver = lazy { EntityWithOptionalStringEnumPropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithPropertyWithDefaultValueJsonEntityParser = lazy { EntityWithPropertyWithDefaultValueJsonParser.EntityParserImpl(this) }
    val entityWithPropertyWithDefaultValueJsonTemplateParser = lazy { EntityWithPropertyWithDefaultValueJsonParser.TemplateParserImpl(this) }
    val entityWithPropertyWithDefaultValueJsonTemplateResolver = lazy { EntityWithPropertyWithDefaultValueJsonParser.TemplateResolverImpl(this) }

    val entityWithPropertyWithDefaultValueNestedJsonEntityParser = lazy { EntityWithPropertyWithDefaultValueNestedJsonParser.EntityParserImpl(this) }
    val entityWithPropertyWithDefaultValueNestedJsonTemplateParser = lazy { EntityWithPropertyWithDefaultValueNestedJsonParser.TemplateParserImpl(this) }
    val entityWithPropertyWithDefaultValueNestedJsonTemplateResolver = lazy { EntityWithPropertyWithDefaultValueNestedJsonParser.TemplateResolverImpl(this) }

    val entityWithRawArrayJsonEntityParser = lazy { EntityWithRawArrayJsonParser.EntityParserImpl(this) }
    val entityWithRawArrayJsonTemplateParser = lazy { EntityWithRawArrayJsonParser.TemplateParserImpl(this) }
    val entityWithRawArrayJsonTemplateResolver = lazy { EntityWithRawArrayJsonParser.TemplateResolverImpl(this) }

    val entityWithRequiredPropertyJsonEntityParser = lazy { EntityWithRequiredPropertyJsonParser.EntityParserImpl(this) }
    val entityWithRequiredPropertyJsonTemplateParser = lazy { EntityWithRequiredPropertyJsonParser.TemplateParserImpl(this) }
    val entityWithRequiredPropertyJsonTemplateResolver = lazy { EntityWithRequiredPropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithSimplePropertiesJsonEntityParser = lazy { EntityWithSimplePropertiesJsonParser.EntityParserImpl(this) }
    val entityWithSimplePropertiesJsonTemplateParser = lazy { EntityWithSimplePropertiesJsonParser.TemplateParserImpl(this) }
    val entityWithSimplePropertiesJsonTemplateResolver = lazy { EntityWithSimplePropertiesJsonParser.TemplateResolverImpl(this) }

    val entityWithStringArrayPropertyJsonEntityParser = lazy { EntityWithStringArrayPropertyJsonParser.EntityParserImpl(this) }
    val entityWithStringArrayPropertyJsonTemplateParser = lazy { EntityWithStringArrayPropertyJsonParser.TemplateParserImpl(this) }
    val entityWithStringArrayPropertyJsonTemplateResolver = lazy { EntityWithStringArrayPropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithStringEnumPropertyJsonEntityParser = lazy { EntityWithStringEnumPropertyJsonParser.EntityParserImpl(this) }
    val entityWithStringEnumPropertyJsonTemplateParser = lazy { EntityWithStringEnumPropertyJsonParser.TemplateParserImpl(this) }
    val entityWithStringEnumPropertyJsonTemplateResolver = lazy { EntityWithStringEnumPropertyJsonParser.TemplateResolverImpl(this) }

    val entityWithStringEnumPropertyWithDefaultValueJsonEntityParser = lazy { EntityWithStringEnumPropertyWithDefaultValueJsonParser.EntityParserImpl(this) }
    val entityWithStringEnumPropertyWithDefaultValueJsonTemplateParser = lazy { EntityWithStringEnumPropertyWithDefaultValueJsonParser.TemplateParserImpl(this) }
    val entityWithStringEnumPropertyWithDefaultValueJsonTemplateResolver = lazy { EntityWithStringEnumPropertyWithDefaultValueJsonParser.TemplateResolverImpl(this) }

    val entityWithoutPropertiesJsonEntityParser = lazy { EntityWithoutPropertiesJsonParser.EntityParserImpl(this) }
    val entityWithoutPropertiesJsonTemplateParser = lazy { EntityWithoutPropertiesJsonParser.TemplateParserImpl(this) }
    val entityWithoutPropertiesJsonTemplateResolver = lazy { EntityWithoutPropertiesJsonParser.TemplateResolverImpl(this) }

    val enumWithDefaultTypeJsonEntityParser = lazy { EnumWithDefaultTypeJsonParser.EntityParserImpl(this) }
    val enumWithDefaultTypeJsonTemplateParser = lazy { EnumWithDefaultTypeJsonParser.TemplateParserImpl(this) }
    val enumWithDefaultTypeJsonTemplateResolver = lazy { EnumWithDefaultTypeJsonParser.TemplateResolverImpl(this) }

    val withDefaultJsonEntityParser = lazy { WithDefaultJsonParser.EntityParserImpl(this) }
    val withDefaultJsonTemplateParser = lazy { WithDefaultJsonParser.TemplateParserImpl(this) }
    val withDefaultJsonTemplateResolver = lazy { WithDefaultJsonParser.TemplateResolverImpl(this) }

    val withoutDefaultJsonEntityParser = lazy { WithoutDefaultJsonParser.EntityParserImpl(this) }
    val withoutDefaultJsonTemplateParser = lazy { WithoutDefaultJsonParser.TemplateParserImpl(this) }
    val withoutDefaultJsonTemplateResolver = lazy { WithoutDefaultJsonParser.TemplateResolverImpl(this) }
}
