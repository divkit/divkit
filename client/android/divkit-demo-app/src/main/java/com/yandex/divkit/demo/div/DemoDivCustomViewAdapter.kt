package com.yandex.divkit.demo.div

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.LinearLayout
import android.widget.Toast
import com.yandex.div.core.DivCustomViewAdapter
import com.yandex.div.core.DivCustomViewAdapter.Companion.getDivChildFactory
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivPreloader
import com.yandex.div2.DivCustom

private const val TAG = "DivCustomViewAdapter"

class DemoDivCustomViewAdapter : DivCustomViewAdapter {

    private val factories = mapOf(
        "new_custom_card_1" to { context: Context -> context.createCustomCard() },
        "new_custom_card_2" to { context: Context -> context.createCustomCard() },
        "new_custom_container_1" to { context: Context -> context.createCustomContainer() },
    )

    override fun isCustomTypeSupported(type: String): Boolean = type in factories.keys

    override fun createView(div: DivCustom, divView: Div2View): View {
        Log.d(TAG, "creating view ${div.id} of type ${div.customType}")
        val customView = factories[div.customType]?.invoke(divView.context)
            ?: throw IllegalStateException("Can not create view for unsupported custom type ${div.customType}")
        if (div.customType == "new_custom_container_1" && div.items != null) {
            div.items!!.forEach {
                val childDivView = getDivChildFactory(divView).createChildView(
                    it,
                    DivStatePath.fromState(divView.id),
                    divView
                )
                (customView as ViewGroup).addView(childDivView)
            }
        }
        return customView
    }

    override fun bindView(customView: View, div: DivCustom, divView: Div2View) {
        Log.d(TAG, "binding view ${div.id} of type ${div.customType}")
        when(div.customType) {
            "new_custom_container_1" -> {
                if (div.items != null && customView is ViewGroup) {
                    if (div.items!!.size != customView.childCount) {
                        throw IllegalStateException("Custom view childСount not equal to div child count! Div type is ${div.customType}")
                    }
                    for (i in div.items!!.indices) {
                        val childDivView = customView.getChildAt(i)
                        val childDiv = div.items!![i]
                        getDivChildFactory(divView).bindChildView(
                            childDivView,
                            childDiv,
                            DivStatePath.fromState(divView.id),
                            divView
                        )
                    }
                }
            }
            else -> {
                if (customView.parent != null) {
                    (customView as? Chronometer)?.bind()
                    return
                }
                Handler (Looper.getMainLooper()).post {
                    (customView as? Chronometer)?.bind()
                }
            }
        }
    }

    override fun release(view: View, div: DivCustom) {
        Log.d(TAG, "custom view released!")
    }

    private fun Context.createCustomCard(): View = Chronometer(this)
    private fun Context.createCustomContainer(): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
    }

    private fun Chronometer.bind() {
        setPadding(30, 30, 30, 30)
        val gd = GradientDrawable()
        gd.orientation = GradientDrawable.Orientation.BL_TR
        gd.colors = intArrayOf(-0xFF0000, -0xFF7F00, -0xF00F00, -0x00FF00, -0x0000FF, -0x2E2B5F, -0x8B00FF)
        background = gd
        textSize = 20f
        setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                isTheFinalCountDown
            } else {
                Toast.makeText(context, "no final countdown for you", Toast.LENGTH_SHORT).show()
            }
        }
        start()
    }
}
