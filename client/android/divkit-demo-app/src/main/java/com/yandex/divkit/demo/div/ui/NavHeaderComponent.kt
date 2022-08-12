package com.yandex.divkit.demo.div.ui

import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.annotation.AttrRes
import com.google.android.material.navigation.NavigationView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.dip
import org.jetbrains.anko.horizontalPadding
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageView
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textResource
import org.jetbrains.anko.textView
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.verticalPadding

open class NavHeaderComponent(
    private val buttons: Map<Int, ButtonDraft>,
    private val onClose: () -> Unit,
    private val caption: String = "",
    private val extraLayout: _LinearLayout.() -> Unit = {}
) : AnkoComponent<NavigationView> {

    override fun createView(ui: AnkoContext<NavigationView>) = with(ui) {
        verticalLayout {
            gravity = Gravity.BOTTOM

            verticalPadding = dip(10)
            horizontalPadding = dip(10)

            imageView(android.R.drawable.sym_def_app_icon)
                    .lparams {
                        gravity = Gravity.START
                        topMargin = dip(TOP_MARGIN)
                    }

            textView {
                text = if (caption.isEmpty()) "ANKO - \uD83D\uDC8F" else "ANKO + $caption = \uD83D\uDC8F"
                topPadding = dip(5)
                bottomPadding = dip(20)
            }

            for ((imageRes, draft) in buttons) {
                linearLayout {
                    imageView {
                        id = IMAGE_VIEW_ID
                        imageResource = imageRes
                        imageTintList = ColorStateList.valueOf(themeColor(android.R.attr.textColorPrimary))
                    }.lparams(width = dip(NAV_BUTTON_SIZE), height = dip(NAV_BUTTON_SIZE))

                    textView {
                        id = TEXT_VIEW_ID
                        textResource = draft.description
                        textSize = 20f
                        gravity = Gravity.CENTER_VERTICAL
                        leftPadding = dip(8)
                    }.lparams(height = dip(NAV_BUTTON_SIZE))

                    onClick {
                        draft.onClickAction()
                        onClose()
                    }

                    topPadding = NAV_BUTTON_PADDING
                }.also { draft.setter?.invoke(it) }
            }
            extraLayout.invoke(this)
        }
    }

    companion object {
        private const val TOP_MARGIN = 25
        private const val NAV_BUTTON_SIZE = 32
        private const val NAV_BUTTON_PADDING = 24

        const val IMAGE_VIEW_ID = 218
        const val TEXT_VIEW_ID = 219
    }
}

fun View.themeColor(@AttrRes id: Int): Int {
    val typedValue = TypedValue()
    val theme: Resources.Theme = context.theme
    theme.resolveAttribute(id, typedValue, true)

    with(context.obtainStyledAttributes(
        typedValue.data, intArrayOf(id)
    )) {
        return getColor(0, -1)
    }
}
