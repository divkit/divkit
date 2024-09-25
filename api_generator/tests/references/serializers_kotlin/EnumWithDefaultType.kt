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

sealed class EnumWithDefaultType : JSONSerializable, Hashable {
    class WithDefaultCase(val value: WithDefault) : EnumWithDefaultType()
    class WithoutDefaultCase(val value: WithoutDefault) : EnumWithDefaultType()

    private var _propertiesHash: Int? = null 
    private var _hash: Int? = null 

    override fun propertiesHash(): Int {
        _propertiesHash?.let {
            return it
        }
        val propertiesHash = this::class.hashCode() + when(this) {
            is WithDefaultCase -> this.value.propertiesHash()
            is WithoutDefaultCase -> this.value.propertiesHash()
        }
       _propertiesHash = propertiesHash
       return propertiesHash
    }

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = this::class.hashCode() + when(this) {
            is WithDefaultCase -> this.value.hash()
            is WithoutDefaultCase -> this.value.hash()
        }
       _hash = hash
       return hash
    }

    fun equals(other: EnumWithDefaultType?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return when(this) {
            is WithDefaultCase -> this.value.equals(other.value() as? WithDefault, resolver, otherResolver)
            is WithoutDefaultCase -> this.value.equals(other.value() as? WithoutDefault, resolver, otherResolver)
        }
    }

    fun value(): Any {
        return when (this) {
            is WithDefaultCase -> value
            is WithoutDefaultCase -> value
        }
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.enumWithDefaultTypeJsonEntityParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {

        @Throws(ParsingException::class)
        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EnumWithDefaultType {
            return builtInParserComponent.enumWithDefaultTypeJsonEntityParser
                .value
                .deserialize(context = env, data = json)
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EnumWithDefaultType(env, json = it) }
    }
}
