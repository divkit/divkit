package com.yandex.div.data

import android.net.Uri
import androidx.annotation.MainThread
import com.yandex.div.core.ObserverList
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.evaluable.types.Color
import com.yandex.div.internal.data.PropertyDelegate
import com.yandex.div.internal.parser.STRING_TO_COLOR_INT
import com.yandex.div.internal.util.ParsingValueUtils.parseAsBoolean
import com.yandex.div.internal.util.ParsingValueUtils.parseAsColor
import com.yandex.div.internal.util.ParsingValueUtils.parseAsDouble
import com.yandex.div.internal.util.ParsingValueUtils.parseAsJsonArray
import com.yandex.div.internal.util.ParsingValueUtils.parseAsJsonObject
import com.yandex.div.internal.util.ParsingValueUtils.parseAsLong
import com.yandex.div.internal.util.ParsingValueUtils.parseAsUri
import com.yandex.div.json.JSONSerializable
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivEvaluableType
import org.json.JSONArray
import org.json.JSONObject

@Mockable
sealed class Variable {
    abstract val name: String
    protected val observers = ObserverList<(Variable) -> Unit>()

    class StringVariable(
        override val name: String,
        val defaultValue: String,
    ) : Variable() {

        @field:Volatile
        internal var value: String = defaultValue
            set(value) = synchronized(this) {
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

        @field:Volatile
        internal var value: Long = defaultValue
            set(value) = synchronized(this) {
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

        @field:Volatile
        internal var value: Boolean = defaultValue
            set(value) = synchronized(this) {
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

        @field:Volatile
        internal var value: Double = defaultValue
            set(value) = synchronized(this) {
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

        @field:Volatile
        internal var value: Color = Color(defaultValue)
            set(value) = synchronized(this) {
                if (field == value) {
                    return
                }
                field = value
                notifyVariableChanged(this)
            }

        @MainThread
        @Throws(VariableMutationException::class)
        fun set(newValue: Color) {
            value = Color(STRING_TO_COLOR_INT(newValue))
        }
    }

    class UrlVariable(
        override val name: String,
        val defaultValue: Uri,
    ) : Variable() {

        @field:Volatile
        internal var value: Uri = defaultValue
            set(value) = synchronized(this) {
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

        @field:Volatile
        internal var value: JSONObject = defaultValue
            set(value) = synchronized(this) {
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

        @field:Volatile
        internal var value: JSONArray = defaultValue
            set(value) = synchronized(this) {
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

    class PropertyVariable @InternalApi constructor(
        override val name: String,
        @property:InternalApi val valueType: DivEvaluableType,
        delegate: PropertyDelegate,
    ) : Variable() {

        @InternalApi
        @field:Volatile
        var delegate: PropertyDelegate = delegate
            set(value) = synchronized(this) {
                field.release()
                field = value
                if (!observers.isEmpty) {
                    field.observe { notifyVariableChanged(this) }
                }
            }

        internal var value: Any
            get() = synchronized(this) { delegate.get() }
            set(value) = synchronized(this) { delegate.set(value) }

        @InternalApi
        val getExpression: Expression<*> get() = synchronized(this) { delegate.getExpression }

        override fun addObserver(observer: (Variable) -> Unit): Unit = synchronized(this) {
            if (observers.isEmpty) {
                delegate.observe { notifyVariableChanged(this) }
            }
            super.addObserver(observer)
        }

        override fun removeObserver(observer: (Variable) -> Unit): Unit = synchronized(this) {
            super.removeObserver(observer)
            if (observers.isEmpty) {
                delegate.release()
            }
        }

        @MainThread
        @InternalApi
        fun set(newValue: Any) {
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
            is PropertyVariable -> value
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
            is PropertyVariable -> throw UnsupportedOperationException("PropertyVariable '$name' has no default value")
        }
    }

    fun addObserver(observer: (Variable) -> Unit): Unit = synchronized(this) {
        observers.addObserver(observer)
    }

    fun removeObserver(observer: (Variable) -> Unit): Unit = synchronized(this) {
        observers.removeObserver(observer)
    }

    protected fun notifyVariableChanged(v: Variable) {
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
            is PropertyVariable -> delegate.set(newValue)
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
            this is PropertyVariable && from is PropertyVariable -> this.value = from.value
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
                is PropertyVariable -> value = newValue
            }
        } catch (_: ClassCastException) {
            throw VariableMutationException("Unable to set value with type ${newValue.javaClass} to $this")
        }
    }

    fun writeToJSON(): JSONObject {
        val serializable: JSONSerializable = when (this) {
            is ArrayVariable -> com.yandex.div2.ArrayVariable(this.name, Expression.constant(this.value))
            is BooleanVariable -> com.yandex.div2.BoolVariable(this.name, Expression.constant(this.value))
            is ColorVariable -> com.yandex.div2.ColorVariable(this.name, Expression.constant(this.value.value))
            is DictVariable -> com.yandex.div2.DictVariable(this.name, Expression.constant(this.value))
            is DoubleVariable -> com.yandex.div2.NumberVariable(this.name, Expression.constant(this.value))
            is IntegerVariable -> com.yandex.div2.IntegerVariable(this.name, Expression.constant(this.value))
            is StringVariable -> com.yandex.div2.StrVariable(this.name, Expression.constant(this.value))
            is UrlVariable -> com.yandex.div2.UrlVariable(this.name, Expression.constant(this.value))
            is PropertyVariable -> this.delegate.toDivVariable()
        }

        return serializable.writeToJSON()
    }
}
