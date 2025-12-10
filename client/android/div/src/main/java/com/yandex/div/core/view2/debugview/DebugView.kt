package com.yandex.div.core.view2.debugview

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.errors.VariableMonitor
import com.yandex.div.core.view2.errors.VariableMonitorView
import com.yandex.div.core.view2.runBindingAction

internal class DebugView(
    private val root: ViewGroup,
    private val divView: Div2View,
    private val errorModel: DebugViewModelProvider,
    private val typefaceProvider: DivTypefaceProvider,
) : Disposable {
    private var counterViewHolder: CounterViewHolder? = null
    private var detailsViewHolder: DetailsViewHolder? = null
    private var detailsPopupWindow: PopupWindow? = null
    private var viewModel: DebugViewModel = DebugViewModel.Hidden
        set(value) {
            field = value
            divView.runBindingAction {
                updateView(value)
            }
        }

    private val modelObservation = errorModel.observeAndGet { m: DebugViewModel -> viewModel = m }

    private fun updateView(new: DebugViewModel) {
        when (new) {
            is DebugViewModel.Details -> {
                removeCounterView()
                tryAddDetailsView()
                detailsViewHolder?.bind(new, errorModel.getAllControllers())
            }

            is DebugViewModel.InfoButton -> {
                removeDetailsView()
                tryAddCounterView()
                counterViewHolder?.bind(new)
            }

            DebugViewModel.Hidden -> {
                removeCounterView()
                removeDetailsView()
            }
        }
    }

    private fun removeCounterView() {
        counterViewHolder?.let { holder ->
            root.removeView(holder.rootView)
        }
        counterViewHolder = null
    }

    @MainThread
    private fun tryAddDetailsView() {
        if (detailsViewHolder != null) {
            return
        }

        val holder = DetailsViewHolder(
            context = root.context,
            errorHandler = errorModel.getErrorHandler(),
            onCloseAction = {
                errorModel.hideDetails()
            },
            onCopyAction = {
                errorModel.copyReportToClipboard()
            },
        )

        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )

        val minSizePx = MIN_SIZE_FOR_DETAILS_DP.dpToPx(root.context.resources.displayMetrics)
        if (root.width < minSizePx || root.height < minSizePx) {
            detailsPopupWindow = PopupWindow(
                holder.rootView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // focusable
            ).apply {
                isOutsideTouchable = true
                showAsDropDown(root, 0, -root.height)
                setOnDismissListener { errorModel.hideDetails() }
            }
        } else {
            root.addView(holder.rootView, layoutParams)
        }
        detailsViewHolder = holder
    }

    @MainThread
    private fun removeDetailsView() {
        detailsViewHolder?.let { holder ->
            if (detailsPopupWindow?.isShowing == true) {
                detailsPopupWindow?.dismiss()
                detailsPopupWindow = null
            } else {
                root.removeView(holder.rootView)
            }
        }
        detailsViewHolder = null
    }

    @MainThread
    private fun tryAddCounterView() {
        if (counterViewHolder != null) {
            return
        }

        val holder = CounterViewHolder(
            context = root.context,
            typefaceProvider = typefaceProvider,
            onCounterClick = { errorModel.onCounterClick() }
        )

        root.addView(holder.rootView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ))
        counterViewHolder = holder
    }

    @MainThread
    override fun close() {
        modelObservation.close()
        counterViewHolder?.let { holder ->
            root.removeView(holder.rootView)
        }
        removeDetailsView()
    }
}

private class CounterViewHolder(
    context: Context,
    private val typefaceProvider: DivTypefaceProvider,
    private val onCounterClick: () -> Unit,
) {
    private val metrics = context.resources.displayMetrics
    private val counterTextView = AppCompatTextView(context).apply {
        setBackgroundResource(R.drawable.error_counter_background)
        textSize = 12f
        setTextColor(Color.BLACK)
        gravity = Gravity.CENTER
        elevation = context.resources.getDimension(R.dimen.div_shadow_elevation)
        typeface = typefaceProvider.regular
        setOnClickListener { onCounterClick() }
    }

    private val announceTextView = AppCompatTextView(context).apply {
        textSize = 12f
        setTextColor(Color.BLACK)
        elevation = context.resources.getDimension(R.dimen.div_shadow_elevation)
        gravity = Gravity.LEFT + Gravity.CENTER
        typeface = typefaceProvider.regular
        maxLines = 1
        maxWidth = 100.dpToPx(metrics)
        ellipsize = TextUtils.TruncateAt.END
    }

    val rootView = LinearLayout(context).apply {
        clipToPadding = false
        clipChildren = false
        orientation = LinearLayout.HORIZONTAL
        val sidePadding = 8.dpToPx(metrics)
        setPadding(sidePadding, sidePadding, 0, 0)
        val counterSize = 24.dpToPx(metrics)
        val layoutParams = ViewGroup.MarginLayoutParams(counterSize, counterSize)
        addView(counterTextView, layoutParams)
        addView(
            announceTextView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, counterSize,
            ).apply {
                marginStart = 4.dpToPx(metrics)
            }
        )
    }

    fun bind(viewModel: DebugViewModel.InfoButton) {
        counterTextView.text = viewModel.label
        counterTextView.setBackgroundResource(viewModel.background)
        announceTextView.text = viewModel.notificationText.orEmpty()
        announceTextView.isVisible = !viewModel.notificationText.isNullOrEmpty()
    }
}

private class DetailsViewHolder(
    context: Context,
    errorHandler: (Throwable) -> Unit,
    private val onCloseAction: () -> Unit,
    private val onCopyAction: () -> Unit,
) {
    private val variableMonitor = VariableMonitor(errorHandler)
    private val monitorView = VariableMonitorView(context, variableMonitor)
    private val controlsContainer = LinearLayout(context).apply {
        val padding = 8.dpToPx(context.resources.displayMetrics)
        setPadding(0, 0, /*right*/padding, 0)
        orientation = LinearLayout.VERTICAL

        val closeView = ImageView(context).apply {
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setOnClickListener { onCloseAction() }
        }
        val copyView = ImageView(context).apply {
            setImageResource(android.R.drawable.ic_menu_save)
            setOnClickListener { onCopyAction() }
        }

        addView(
            closeView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        )
        addView(
            copyView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        )
    }

    private val hotReloadSwitchView = SwitchCompat(context).apply {
        setTextColor(Color.WHITE)
        thumbTextPadding = 8.dpToPx(context.resources.displayMetrics)
    }
    private val hotReloadTitleView = AppCompatTextView(context).apply {
        setTextColor(Color.WHITE)
        textSize = 14f
        gravity = Gravity.LEFT
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1f
        }
    }
    private val hotReloadStatusView = AppCompatTextView(context).apply {
        setTextColor(Color.LTGRAY)
        textSize = 12f
        gravity = Gravity.LEFT
    }
    private val hotReloadHostInput = AppCompatEditText(context).apply {
        setTextColor(Color.WHITE)
        textSize = 12f
        setHintTextColor(Color.LTGRAY)
        hint = "server address"
    }
    private val hotReloadDocLinkView = AppCompatTextView(context).apply {
        setTextColor(Color.BLUE)
        textSize = 12f
        gravity = Gravity.LEFT
        movementMethod = LinkMovementMethod.getInstance()
        isVisible = false
    }

    private val hotReloadHostTitle = AppCompatTextView(context).apply {
        setTextColor(Color.LTGRAY)
        textSize = 12f
        gravity = Gravity.LEFT
        text = "Listening at:"
    }

    private val hotReloadHostContainer = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        addView(hotReloadHostTitle)
        addView(
            hotReloadHostInput,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }
    private val hotReloadContainer = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        visibility = View.GONE

        addView(
            hotReloadSwitchView,
            ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        addView(hotReloadHostContainer)
        addView(hotReloadStatusView)
        addView(hotReloadDocLinkView)
    }

    private val errorsOutput = AppCompatTextView(context).apply {
        setTextColor(Color.WHITE)
        gravity = Gravity.LEFT
    }

    private val detailsRootContainer: View = run {
        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL

        root.addView(
            hotReloadContainer, ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        root.addView(errorsOutput)
        root
    }

    private val topPanelView = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL

        addView(
            controlsContainer, LinearLayout.LayoutParams(
                32.dpToPx(rootView.resources.displayMetrics),
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        )
        addView(
            detailsRootContainer, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        )
    }

    val rootView: LinearLayout = run {
        val rootView = LinearLayout(context)
        val padding = 8.dpToPx(rootView.resources.displayMetrics)
        rootView.setPadding(padding, padding, padding, padding)
        rootView.orientation = LinearLayout.VERTICAL
        rootView.setBackgroundColor(Color.argb(186, 0, 0, 0))
        rootView.elevation = rootView.resources.getDimension(R.dimen.div_shadow_elevation)

        rootView.addView(
            topPanelView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        )
        rootView.addView(
            monitorView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        )
        rootView
    }

    fun bind(new: DebugViewModel.Details, variableControllers: Map<String, VariableController>) {
        errorsOutput.text = new.errorsAndWarningsOverview
        variableMonitor.controllerMap = variableControllers
        bindHotReload(new.hotReload)
    }

    private fun bindHotReload(viewModel: DebugViewModel.Details.HotReloadViewModel?) {
        if (viewModel == null) {
            hotReloadContainer.visibility = View.GONE
            return
        }

        hotReloadContainer.visibility = View.VISIBLE

        hotReloadSwitchView.text = viewModel.title
        hotReloadSwitchView.isChecked = viewModel.switchChecked
        hotReloadSwitchView.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onSwitchClick(isChecked)
        }

        hotReloadTitleView.text = viewModel.title
        hotReloadStatusView.text = viewModel.description
        hotReloadStatusView.isVisible = viewModel.description != null
        if (hotReloadHostInput.text.toString() != viewModel.address) {
            hotReloadHostInput.setText(viewModel.address)
        }
        hotReloadHostInput.doAfterTextChanged {
            viewModel.onAddressChanged(it.toString())
        }

        hotReloadHostContainer.isVisible = viewModel.switchChecked
        hotReloadDocLinkView.isVisible = viewModel.docLink != null

        val linkTitle = viewModel.docLink.orEmpty()
        val spannableString = SpannableString(linkTitle)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                viewModel.onDocLinkClick()
            }
        }
        spannableString.setSpan(
            clickableSpan, 0, linkTitle.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        hotReloadDocLinkView.text = spannableString
    }
}

private const val MIN_SIZE_FOR_DETAILS_DP = 150
