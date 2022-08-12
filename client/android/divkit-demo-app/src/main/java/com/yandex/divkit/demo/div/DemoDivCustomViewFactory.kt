package com.yandex.divkit.demo.div

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.yandex.div.core.DivCustomViewFactory
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivCustom

class DemoDivCustomViewFactory: DivCustomViewFactory {

    override fun create(data: DivCustom, divView: Div2View, listener: DivCustomViewFactory.OnViewCreatedListener) {
        val view = when(data.customType) {
            "old_custom_card_1" -> divView.context.createCustomCard("hi, i'm old card")
            "old_custom_card_2" -> divView.context.createCustomCard("and i'm old as well!")
            else -> EditText(divView.context)
        }
        listener.onCreate(view)
    }

    private fun Context.createCustomCard(message: String): View {
        val textView = TextView(this)
        textView.text = message
        return textView
    }
}