package com.yandex.div.data

import android.net.Uri
import androidx.annotation.MainThread
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.util.Assert
import com.yandex.div.json.STRING_TO_COLOR_INT
import com.yandex.div.json.toBoolean
import com.yandex.div.util.SynchronizedList

@Mockable
sealed class Variable {
    abstract val name: String
    private val observers = SynchronizedList<(Variable) -> Unit>()

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
        val defaultValue: Int,
    ) : Variable() {
        internal var value: Int = defaultValue
            set(value) {
                if (field == value) {
                    return
                }
                field = value
                notifyVariableChanged(this)
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
    }

    class ColorVariable(
        override val name: String,
        val defaultValue: Int,
    ) : Variable() {
        internal var value: Int = defaultValue
            set(value) {
                if (field == value) {
                    return
                }
                field = value
                notifyVariableChanged(this)
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
    }

    fun getValue(): Any {
        return when (this) {
            is StringVariable -> value
            is IntegerVariable -> value
            is BooleanVariable -> value
            is DoubleVariable -> value
            is ColorVariable -> value
            is UrlVariable -> value
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
        }
    }

    fun addObserver(observer: (Variable) -> Unit) {
        observers.add(observer)
    }

    fun removeObserver(observer: (Variable) -> Unit) {
        observers.remove(observer)
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
            is IntegerVariable -> value = newValue.parseAsInt()
            is BooleanVariable -> value = newValue.parseAsBoolean()
            is DoubleVariable -> value = newValue.parseAsDouble()
            is ColorVariable -> {
                val color = STRING_TO_COLOR_INT(newValue) ?: throw VariableMutationException(
                    "Wrong value format for color variable: '$newValue'")
                value = color
            }
            is UrlVariable -> value = newValue.parseAsUri()
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
            else -> throw VariableMutationException("Setting value to $this from $from not supported!")
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
            return parseAsInt().toBoolean()
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
}
