package com.yandex.div.core.animation

import android.util.Log
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.types.Color


internal object IntegerValueProperty : IntegerProperty<Variable.IntegerVariable>("value") {

    override fun setValue(target: Variable.IntegerVariable, value: Int) {
        target.setValueDirectly(value.toLong())
    }

    override fun get(target: Variable.IntegerVariable): Int {
        return (target.getValue() as Long).toInt()
    }
}

internal object NumberValueProperty : FloatProperty<Variable.DoubleVariable>("value") {

    override fun setValue(target: Variable.DoubleVariable, value: Float) {
        Log.i("NumberValueProperty", "set variable value: $value")
        target.setValueDirectly(value.toDouble())
    }

    override fun get(target: Variable.DoubleVariable): Float {
        return (target.getValue() as Double).toFloat()
    }
}

internal object ColorIntValueProperty : IntegerProperty<Variable.ColorVariable>("value") {

    override fun setValue(target: Variable.ColorVariable, value: Int) {
        target.setValueDirectly(Color(value))
    }

    override fun get(target: Variable.ColorVariable): Int {
        return (target.getValue() as Color).value
    }
}
