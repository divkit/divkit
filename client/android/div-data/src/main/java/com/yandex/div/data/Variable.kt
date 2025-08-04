package com.yandex.div.data

import android.net.Uri
import androidx.annotation.MainThread
import com.yandex.div.core.ObserverList
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.evaluable.types.Color
import com.yandex.div.internal.Assert
import com.yandex.div.internal.parser.STRING_TO_COLOR_INT
import com.yandex.div.internal.util.toBoolean
import com.yandex.div.json.JSONSerializable
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.BoolVariable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@Mockable
sealed class Variable {
    abstract val name: String
    private val observers = ObserverList<(Variable) -> Unit>()

    class StringVariable(
        override val name: String,
        val defaultValue: String,
    ) : Variable() {
        internal var value: String = defaultValue
            set(value) {
                if (field == value) {
                    return
                }
                field = value
                notifyVariableChanged(this)
            }
    }

    class IntegerVariable(
        override val name: String,
        val defaultValue: Long,
    ) : Variable() {
        internal var value: Long = defaultValue
            set(value) {
                if (field == value) {
                    return
                }
                field = value
                notifyVariableChanged(this)
            }

        @MainThread
        fun set(newValue: Long) {
            value = newValue
        }
    }

    class BooleanVariable(
        override val name: String,
        val defaultValue: Boolean
    ) : Variable() {
        internal var value: Boolean = defaultValue
            set(value) {
                if (field == value) {
                    return
                }
                field = value
                notifyVariableChanged(this)
            }

        @MainThread
        fun set(newValue: Boolean) {
            value = newValue
        }
    }

    class DoubleVariable(
        override val name: String,
        val defaultValue: Double
    ) : Variable() {
        internal var value: Double = defaultValue
            set(value) {
                if (field == value) {
                    return
                }
                field = value
                notifyVariableChanged(this)
            }

        @MainThread
        fun set(newValue: Double) {
            value = newValue
        }
    }

    class ColorVariable(
        override val name: String,
        val defaultValue: Int,
    ) : Variable() {
        internal var value: Color = Color(defaultValue)
            set(value) {
                if (field == value) {
                    return
                }
                field = value
                notifyVariableChanged(this)
            }

        @MainThread
        @Throws(VariableMutationException::class)
        fun set(newValue: Color) {
            val color = STRING_TO_COLOR_INT(newValue) ?: throw VariableMutationException(
                    "Wrong value format for color variable: '$newValue'")
            value = Color(color)
        }
    }

    class UrlVariable(
        override val name: String,
        val defaultValue: Uri,
    ) : Variable() {
        internal var value: Uri = defaultValue
            set(value) {
                if (field == value) {
                    return
                }
                field = value
                notifyVariableChanged(this)
            }

        @MainThread
        fun set(newValue: Uri) {
            value = newValue
        }
    }

    class DictVariable(
        override val name: String,
        val defaultValue: JSONObject,
    ) : Variable() {
        internal var value: JSONObject = defaultValue
            set(value) {
                if (field == value) {
                    return
                }
                field = value
                notifyVariableChanged(this)
            }

        @MainThread
        fun set(newValue: JSONObject) {
            value = newValue
        }
    }

    class ArrayVariable(
        override val name: String,
        val defaultValue: JSONArray
    ): Variable() {
        internal var value: JSONArray = defaultValue
            set(value) {
                if (field == value) {
                    return
                }
                field = value
                notifyVariableChanged(this)
            }

        @MainThread
        fun set(newValue: JSONArray) {
            value = newValue
        }
    }

    class PropertyVariable(
        override val name: String,
    ): Variable()

    fun getValue(): Any {
        return when (this) {
            is StringVariable -> value
            is IntegerVariable -> value
            is BooleanVariable -> value
            is DoubleVariable -> value
            is ColorVariable -> value
            is UrlVariable -> value
            is DictVariable -> value
            is ArrayVariable -> value
            is PropertyVariable -> TODO("Support property variables")
        }
    }

    fun getDefaultValue(): Any {
        return when (this) {
            is StringVariable -> defaultValue
            is IntegerVariable -> defaultValue
            is BooleanVariable -> defaultValue
            is DoubleVariable -> defaultValue
            is ColorVariable -> defaultValue
            is UrlVariable -> defaultValue
            is DictVariable -> defaultValue
            is ArrayVariable -> defaultValue
            is PropertyVariable -> TODO("Support property variables")
        }
    }

    fun addObserver(observer: (Variable) -> Unit) {
        observers.addObserver(observer)
    }

    fun removeObserver(observer: (Variable) -> Unit) {
        observers.removeObserver(observer)
    }

    protected fun notifyVariableChanged(v: Variable) {
        Assert.assertMainThread()
        observers.forEach { it.invoke(v) }
    }

    @MainThread
    @Throws(VariableMutationException::class)
    fun set(newValue: String) {
        return when (this) {
            is StringVariable -> value = newValue
            is IntegerVariable -> value = newValue.parseAsLong()
            is BooleanVariable -> value = newValue.parseAsBoolean()
            is DoubleVariable -> value = newValue.parseAsDouble()
            is ColorVariable -> value = newValue.parseAsColor()
            is UrlVariable -> value = newValue.parseAsUri()
            is DictVariable -> value = newValue.parseAsJsonObject()
            is ArrayVariable -> value = newValue.parseAsJsonArray()
            is PropertyVariable -> TODO("Support property variables")
        }
    }

    @MainThread
    @Throws(VariableMutationException::class)
    fun setValue(from: Variable) {
        when {
            this is StringVariable && from is StringVariable -> this.value = from.value
            this is IntegerVariable && from is IntegerVariable -> this.value = from.value
            this is BooleanVariable && from is BooleanVariable -> this.value = from.value
            this is DoubleVariable && from is DoubleVariable -> this.value = from.value
            this is ColorVariable && from is ColorVariable -> this.value = from.value
            this is UrlVariable && from is UrlVariable -> this.value = from.value
            this is DictVariable && from is DictVariable -> this.value = from.value
            this is ArrayVariable && from is ArrayVariable -> this.value = from.value
            this is PropertyVariable && from is PropertyVariable -> TODO("Support property variables")
            else -> throw VariableMutationException("Setting value to $this from $from not supported!")
        }
    }

    @InternalApi
    @MainThread
    @Throws(VariableMutationException::class)
    fun setValueDirectly(newValue: Any) {
        try {
            return when (this) {
                is StringVariable -> value = newValue as String
                is IntegerVariable -> value = (newValue as Number).toLong()
                is BooleanVariable -> value = newValue as Boolean
                is DoubleVariable -> value = (newValue as Number).toDouble()
                is ColorVariable -> value = newValue as Color
                is UrlVariable -> value = newValue as Uri
                is DictVariable -> value = newValue as JSONObject
                is ArrayVariable -> value = newValue as JSONArray
                is PropertyVariable -> TODO("Support property variables")
            }
        } catch (e: ClassCastException) {
            throw VariableMutationException("Unable to set value with type ${newValue.javaClass} to $this")
        }
    }

    private fun String.parseAsLong(): Long {
        return try {
            this.toLong()
        } catch (e: NumberFormatException) {
            throw VariableMutationException(cause = e)
        }
    }

    private fun String.parseAsInt(): Int {
        return try {
            this.toInt()
        } catch (e: NumberFormatException) {
            throw VariableMutationException(cause = e)
        }
    }

    private fun String.parseAsBoolean(): Boolean {
        return toBooleanStrictOrNull() ?: parseAsInt().toBoolean()
            ?: throw VariableMutationException("Unable to convert $this to boolean")
    }

    private fun String.parseAsDouble(): Double {
        return try {
            this.toDouble()
        } catch (e: NumberFormatException) {
            throw VariableMutationException(cause = e)
        }
    }

    private fun String.parseAsColor(): Color {
        val color = STRING_TO_COLOR_INT(this)
            ?: throw VariableMutationException("Wrong value format for color variable: '$this'")
        return Color(color)
    }

    private fun String.parseAsUri(): Uri {
        return try {
            Uri.parse(this)
        } catch (e: IllegalArgumentException) {
            throw VariableMutationException(cause = e)
        }
    }

    private fun String.parseAsJsonObject(): JSONObject {
        return try {
            JSONObject(this)
        } catch (e: JSONException) {
            throw VariableMutationException(cause = e)
        }
    }

    private fun String.parseAsJsonArray(): JSONArray {
        return try {
            JSONArray(this)
        } catch (e: JSONException) {
            throw VariableMutationException(cause = e)
        }
    }

    fun writeToJSON(): JSONObject {
        val serializable: JSONSerializable = when (this) {
            is ArrayVariable -> com.yandex.div2.ArrayVariable(this.name, Expression.constant(this.value))
            is BooleanVariable -> BoolVariable(this.name, Expression.constant(this.value))
            is ColorVariable -> com.yandex.div2.ColorVariable(this.name, Expression.constant(this.value.value))
            is DictVariable -> com.yandex.div2.DictVariable(this.name, Expression.constant(this.value))
            is DoubleVariable -> com.yandex.div2.NumberVariable(this.name, Expression.constant(this.value))
            is IntegerVariable -> com.yandex.div2.IntegerVariable(this.name, Expression.constant(this.value))
            is StringVariable -> com.yandex.div2.StrVariable(this.name, Expression.constant(this.value))
            is UrlVariable -> com.yandex.div2.UrlVariable(this.name, Expression.constant(this.value))
            is PropertyVariable -> TODO("Support property variables")
        }

        return serializable.writeToJSON()
    }
}
