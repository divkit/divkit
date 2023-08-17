package com.yandex.div.data

import android.net.Uri
import androidx.annotation.MainThread
import com.yandex.div.core.ObserverList
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.evaluable.types.Color
import com.yandex.div.internal.Assert
import com.yandex.div.internal.parser.STRING_TO_COLOR_INT
import com.yandex.div.internal.parser.toBoolean
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
        val defaultValue: Boolean) : Variable() {
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

    class DoubleVariable(override val name: String,
                         val defaultValue: Double) : Variable() {
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
            is ColorVariable -> {
                val color = STRING_TO_COLOR_INT(newValue) ?: throw VariableMutationException(
                    "Wrong value format for color variable: '$newValue'")
                value = Color(color)
            }
            is UrlVariable -> value = newValue.parseAsUri()
            is DictVariable -> value = newValue.parseAsJsonObject()
            is ArrayVariable -> value = newValue.parseAsJsonArray()
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
            else -> throw VariableMutationException("Setting value to $this from $from not supported!")
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
        try {
            return toBooleanStrictOrNull() ?: parseAsInt().toBoolean()
        } catch (e: IllegalArgumentException) {
            throw VariableMutationException(cause = e)
        }
    }

    private fun String.parseAsDouble(): Double {
        return try {
            this.toDouble()
        } catch (e: NumberFormatException) {
            throw VariableMutationException(cause = e)
        }
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
}
