// Generated code. Do not modify.

package com.yandex.div.reference

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.json.JsonArray
import com.yandex.div.core.json.JsonObject
import com.yandex.div.core.type.Color
import com.yandex.div.core.type.Url
import com.yandex.div.model.entity.*
import com.yandex.div.model.expression.*
import com.yandex.div.model.entity.EntityTemplate as Template

public sealed class EnumWithDefaultTypeTemplate : Template<EnumWithDefaultType> {
    public class WithDefaultCase(public val value: WithDefaultTemplate) : EnumWithDefaultTypeTemplate()
    public class WithoutDefaultCase(public val value: WithoutDefaultTemplate) : EnumWithDefaultTypeTemplate()

    public fun value(): Any {
        return when (this) {
            is WithDefaultCase -> value
            is WithoutDefaultCase -> value
        }
    }

    public val type: String
        get() {
            return when (this) {
                is WithDefaultCase -> WithDefaultTemplate.TYPE
                is WithoutDefaultCase -> WithoutDefaultTemplate.TYPE
            }
        }
}
