package com.yandex.div.core.animation

import android.util.Property
import com.yandex.div.evaluable.types.Color

internal abstract class ColorProperty<T>(name: String) : Property<T, Color>(Color::class.java, name)
