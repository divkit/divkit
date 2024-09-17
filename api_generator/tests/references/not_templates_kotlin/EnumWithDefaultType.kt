// Generated code. Do not modify.

package com.yandex.div2

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import com.yandex.div.core.annotations.Mockable
import java.io.IOException
import java.util.BitSet
import org.json.JSONObject
import com.yandex.div.data.*
import org.json.JSONArray

@Mockable
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
        return when (this) {
            is WithDefaultCase -> value.writeToJSON()
            is WithoutDefaultCase -> value.writeToJSON()
        }
    }

    companion object {

        @Throws(ParsingException::class)
        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EnumWithDefaultType {
            val logger = env.logger
            val type: String = json.readOptional("type", logger = logger, env = env) ?: WithDefault.TYPE
            when (type) {
                WithDefault.TYPE -> return WithDefaultCase(WithDefault(env, json))
                WithoutDefault.TYPE -> return WithoutDefaultCase(WithoutDefault(env, json))
                else -> throw typeMismatch(json = json, key = "type", value = type)
            }
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EnumWithDefaultType(env, json = it) }
    }
}
