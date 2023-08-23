package com.yandex.div.core

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.SystemClock
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.MainThread
import androidx.annotation.StyleRes
import androidx.core.view.LayoutInflaterCompat
import androidx.lifecycle.LifecycleOwner
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.core.expression.variables.GlobalVariableController
import com.yandex.div.core.view2.Div2View

/**
 * Context to be used to create instance of [Div2View]
 * Note: if you want to inflate a [Div2View] from XML layout file you should get an inflater as following
 * <pre>
 *     {@code
 *     val div2Context = Div2Context(activity, divConfiguration)
 *     val inflater = div2Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
 *     }
 * </pre>
 * or
 * <pre>
 *     {@code
 *     val div2Context = Div2Context(activity, divConfiguration)
 *     val inflater = LayoutInflater.from(div2Context)
 *     }
 * </pre>
 */
@Mockable
class Div2Context @MainThread private constructor(
    private val baseContext: ContextThemeWrapper,
    internal val div2Component: Div2Component,
    internal val lifecycleOwner: LifecycleOwner? = null
) : ContextWrapper(baseContext) {

    val globalVariableController: GlobalVariableController
        get() = div2Component.globalVariableController

    private var inflater: LayoutInflater? = null

    @Deprecated("use Div2Context(ContextThemeWrapper, DivConfiguration, LifecycleOwner) instead")
    @JvmOverloads
    constructor(
        baseContext: ContextThemeWrapper,
        configuration: DivConfiguration,
        @StyleRes themeId: Int = R.style.Div_Theme
    ) : this(baseContext, configuration, themeId, null)

    constructor(
        baseContext: ContextThemeWrapper,
        configuration: DivConfiguration,
        @StyleRes themeId: Int = R.style.Div_Theme,
        lifecycleOwner: LifecycleOwner? = null
    ) : this(
        baseContext,
        DivKit.getInstance(baseContext).component
            .div2Component()
            .baseContext(baseContext)
            .configuration(configuration)
            .themeId(themeId)
            .divCreationTracker(DivCreationTracker(SystemClock.uptimeMillis()))
            .globalVariableController(configuration.globalVariableController)
            .build(),
        lifecycleOwner
    )

    @Deprecated("use Div2Context(ContextThemeWrapper, DivConfiguration, LifecycleOwner) instead")
    constructor(
        activity: Activity,
        configuration: DivConfiguration
    ) : this(
        activity as ContextThemeWrapper,
        configuration,
        R.style.Div_Theme,
        activity as? LifecycleOwner
    )

    init {
        div2Component.divCreationTracker.onContextCreationFinished()
    }

    override fun getSystemService(name: String): Any? {
        return if (Context.LAYOUT_INFLATER_SERVICE == name) {
            getLayoutInflater()
        } else {
            baseContext.getSystemService(name)
        }
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
                LayoutInflaterCompat.setFactory2(inflater as LayoutInflater, Div2InflaterFactory(this))
                this.inflater = inflater
            }
            return inflater
        }
    }

    fun warmUp() {
        div2Component.div2Builder
    }

    @Deprecated("use warmUp() instead")
    fun warmUp2() = warmUp()

    fun resetVisibilityCounters() {
        div2Component.visibilityActionDispatcher.reset()
    }

    fun childContext(baseContext: ContextThemeWrapper): Div2Context {
        return Div2Context(baseContext, div2Component, lifecycleOwner)
    }

    /**
     * Consider using to define a context for [Div2View] with a shorter lifecycle
     * instead of creating a new instance of [Div2Context].
     */
    fun childContext(lifecycleOwner: LifecycleOwner?): Div2Context {
        return Div2Context(baseContext, div2Component, lifecycleOwner)
    }

    /**
     * Consider using to define a context for [Div2View] with a shorter lifecycle
     * instead of creating a new instance of [Div2Context].
     */
    fun childContext(baseContext: ContextThemeWrapper, lifecycleOwner: LifecycleOwner?): Div2Context {
        return Div2Context(baseContext, div2Component, lifecycleOwner)
    }

    private class Div2InflaterFactory(
        private val div2Context: Div2Context
    ) : LayoutInflater.Factory2 {
        override fun onCreateView(
            parent: View?,
            name: String,
            context: Context,
            attrs: AttributeSet
        ): View? = onCreateView(name, context, attrs)

        override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
            return if (isDiv2View(name)) {
                Div2View(div2Context, attrs)
            } else {
                null
            }
        }

        private fun isDiv2View(viewClassName: String): Boolean {
            return DIV_VIEW_CLASS_NAME == viewClassName || DIV_VIEW_SIMPLE_CLASS_NAME == viewClassName
        }

        companion object {
            private const val DIV_VIEW_CLASS_NAME = "com.yandex.div.core.view2.Div2View"
            private const val DIV_VIEW_SIMPLE_CLASS_NAME = "Div2View"
        }
    }
}
