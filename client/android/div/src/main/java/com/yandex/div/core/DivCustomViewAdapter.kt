package com.yandex.div.core

import android.view.View
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivCustom

@Deprecated("deprecated", replaceWith = ReplaceWith("DivCustomContainerViewAdapter"))
interface DivCustomViewAdapter {

    /**
     * Create view corresponding to [DivCustom.customType].
     * If [DivCustom] has non null field [DivCustom.items],
     * you need to call [DivCustomContainerChildFactory.createChildView]
     * and then [ViewGroup.addView(div)] by yourself!
     */
    fun createView(
        div: DivCustom,
        divView: Div2View
    ): View

    /**
     * Bind [DivCustom] to view created in [createView].
     * If [DivCustom] has non null field [DivCustom.items],
     * call [DivCustomContainerChildFactory.bindChildView]
     * to bind your views with div views.
     */
    fun bindView(
        view: View,
        div: DivCustom,
        divView: Div2View
    )

    /**
     * @param type is [DivCustom.customType].
     */
    fun isCustomTypeSupported(type: String): Boolean

    fun release(view: View, div: DivCustom)

    fun preload(div: DivCustom, callBack: DivPreloader.Callback): DivPreloader.PreloadReference =
        DivPreloader.PreloadReference.EMPTY

    companion object {

        /**
         * Getter for [DivCustomContainerChildFactory]. Call it's methods to create and
         * bind child div views
         */
        fun getDivChildFactory(div2View: Div2View): DivCustomContainerChildFactory {
            return div2View.getCustomContainerChildFactory()
        }

        @JvmField
        val STUB = object : DivCustomViewAdapter {
            override fun createView(div: DivCustom, divView: Div2View): View {
                throw UnsupportedOperationException()
            }

            override fun bindView(view: View, div: DivCustom, divView: Div2View) = Unit

            override fun isCustomTypeSupported(type: String): Boolean {
                return false
            }

            override fun release(view: View, div: DivCustom) = Unit

            override fun preload(div: DivCustom, callBack: DivPreloader.Callback): DivPreloader.PreloadReference =
                    DivPreloader.PreloadReference.EMPTY

        }
    }
}
