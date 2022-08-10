package com.yandex.div.util

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import androidx.core.view.LayoutInflaterCompat

abstract class CustomInflaterContext(baseContext: Context) : ContextWrapper(baseContext) {

    private var inflater: LayoutInflater? = null

    override fun getSystemService(name: String): Any? {
        return if (Context.LAYOUT_INFLATER_SERVICE == name) {
            getLayoutInflater()
        } else baseContext.getSystemService(name)
    }

    private fun getLayoutInflater(): LayoutInflater? {
        var inflater = this.inflater
        if (inflater != null) {
            return inflater
        }
        synchronized(this) {
            inflater = this.inflater
            if (inflater == null) {
                inflater = LayoutInflater.from(baseContext).cloneInContext(this)
                LayoutInflaterCompat.setFactory2(inflater as LayoutInflater, createInflaterFactory())
                this.inflater = inflater
            }
            return inflater
        }
    }

    abstract fun createInflaterFactory(): LayoutInflater.Factory2
}
