package com.yandex.div.internal.widget

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.annotation.IntDef
import androidx.core.view.GravityCompat

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    Gravity.LEFT,
    Gravity.START,
    Gravity.TOP,
    Gravity.RIGHT,
    Gravity.END,
    Gravity.BOTTOM,
    Gravity.CENTER_HORIZONTAL,
    Gravity.CENTER_VERTICAL,
    DivGravity.SPACE_AROUND_HORIZONTAL,
    DivGravity.SPACE_AROUND_VERTICAL,
    DivGravity.SPACE_BETWEEN_HORIZONTAL,
    DivGravity.SPACE_BETWEEN_VERTICAL,
    DivGravity.SPACE_EVENLY_HORIZONTAL,
    DivGravity.SPACE_EVENLY_VERTICAL,
    flag = true
)
annotation class DivGravity {
    companion object {
        private const val AXIS_SPACE_AROUND = 0x01000000
        private const val AXIS_SPACE_BETWEEN = 0x02000000
        private const val AXIS_SPACE_EVENLY = 0x04000000

        @SuppressLint("WrongConstant")
        const val SPACE_AROUND_HORIZONTAL = AXIS_SPACE_AROUND shl Gravity.AXIS_X_SHIFT
        @SuppressLint("WrongConstant")
        const val SPACE_AROUND_VERTICAL = AXIS_SPACE_AROUND shl Gravity.AXIS_Y_SHIFT
        @SuppressLint("WrongConstant")
        const val SPACE_BETWEEN_HORIZONTAL = AXIS_SPACE_BETWEEN shl Gravity.AXIS_X_SHIFT
        @SuppressLint("WrongConstant")
        const val SPACE_BETWEEN_VERTICAL = AXIS_SPACE_BETWEEN shl Gravity.AXIS_Y_SHIFT
        @SuppressLint("WrongConstant")
        const val SPACE_EVENLY_HORIZONTAL = AXIS_SPACE_EVENLY shl Gravity.AXIS_X_SHIFT
        @SuppressLint("WrongConstant")
        const val SPACE_EVENLY_VERTICAL = AXIS_SPACE_EVENLY shl Gravity.AXIS_Y_SHIFT

        @SuppressLint("WrongConstant")
        private const val EXTRA_GRAVITY_AXIS = AXIS_SPACE_AROUND or AXIS_SPACE_BETWEEN or AXIS_SPACE_EVENLY

        @SuppressLint("WrongConstant")
        const val HORIZONTAL_GRAVITY_MASK = GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK or
            EXTRA_GRAVITY_AXIS shl Gravity.AXIS_X_SHIFT

        @SuppressLint("WrongConstant")
        const val VERTICAL_GRAVITY_MASK = Gravity.AXIS_SPECIFIED or Gravity.AXIS_PULL_BEFORE or
            Gravity.AXIS_PULL_AFTER or EXTRA_GRAVITY_AXIS shl Gravity.AXIS_Y_SHIFT
    }
}
