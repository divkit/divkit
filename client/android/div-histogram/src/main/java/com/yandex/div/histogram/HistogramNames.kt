package com.yandex.div.histogram

private const val DIV_RENDER_PREFIX = "Div.Render"
private const val DIV_PARSING_PREFIX = "Div.Parsing"

/**
 * Histogram name for reporting time in milliseconds taken by [Div2View.onLayout].
 */
internal const val DIV_RENDER_LAYOUT = "$DIV_RENDER_PREFIX.Layout"

/**
 *  Histogram name for reporting time in milliseconds taken by [Div2View.onMeasure].
 */
internal const val DIV_RENDER_MEASURE = "$DIV_RENDER_PREFIX.Measure"

/**
 *  Histogram name for reporting time in milliseconds taken by [Div2View.onDraw].
 */
internal const val DIV_RENDER_DRAW = "$DIV_RENDER_PREFIX.Draw"

/**
 * Histogram name for reporting time in milliseconds taken to render [Div2View].
 */
internal const val DIV_RENDER_TOTAL = "$DIV_RENDER_PREFIX.Total"

/**
 * Histogram name for reporting time taken by synchronous [Div2View.setData] before binding or
 * rebinding starts: runtime store, state variables, data comparison and preloading. Not included in
 * [DIV_RENDER_TOTAL] and filtered by [RenderConfiguration.prepareFilter].
 */
internal const val DIV_RENDER_PREPARE = "$DIV_RENDER_PREFIX.Prepare"

internal const val DIV_PARSING_DATA = "$DIV_PARSING_PREFIX.Data"
internal const val DIV_PARSING_TEMPLATES = "$DIV_PARSING_PREFIX.Templates"
internal const val DIV_PARSING_JSON = "$DIV_PARSING_PREFIX.JSON"

internal const val DIV_BINDING_HISTOGRAM = "Div.Binding"
internal const val DIV_REBINDING_HISTOGRAM = "Div.Rebinding"

/**
 * Consecutive parts of [DIV_BINDING_HISTOGRAM], which add up to it. Reported only for the first
 * synchronous binding without bind-on-attach.
 * [DIV_BINDING_SETUP]: cleanup, the root view and runtimes.
 * [DIV_BINDING_VIEWS]: binding of the view tree from the root, including creation of child views.
 * [DIV_BINDING_FINISH]: adding the root view, visibility tracking and variable triggers.
 */
internal const val DIV_BINDING_SETUP = "$DIV_BINDING_HISTOGRAM.Setup"
internal const val DIV_BINDING_VIEWS = "$DIV_BINDING_HISTOGRAM.Views"
internal const val DIV_BINDING_FINISH = "$DIV_BINDING_HISTOGRAM.Finish"

private const val DIV_ASYNC_BINDING_PREFIX = "Div.AsyncBinding"

/**
 * Consecutive phases of [Div2View.setDataAsync] and [Div2View.setDataWithStatesAsync], which add
 * up to [DIV_ASYNC_BINDING_TOTAL]. Reported only for a binding of new data that the main thread
 * applied: not for cancelled or failed tasks, not for requests with the current data and not when
 * bind-on-attach defers binding of views until the detached [Div2View] is attached.
 * [DIV_ASYNC_BINDING_BACKGROUND_WAIT]: from the call until work starts on the binding thread.
 * [DIV_ASYNC_BINDING_BACKGROUND]: work on the binding thread, including waiting for the main-thread
 * work it runs synchronously, for example binding of `div-custom` or complex rebind.
 * [DIV_ASYNC_BINDING_MAIN_WAIT]: from its end until the main-thread continuation starts.
 * [DIV_ASYNC_BINDING_MAIN]: applying the deferred changes on the main thread, before the host
 * callback.
 */
internal const val DIV_ASYNC_BINDING_BACKGROUND_WAIT = "$DIV_ASYNC_BINDING_PREFIX.BackgroundWait"
internal const val DIV_ASYNC_BINDING_BACKGROUND = "$DIV_ASYNC_BINDING_PREFIX.Background"
internal const val DIV_ASYNC_BINDING_MAIN_WAIT = "$DIV_ASYNC_BINDING_PREFIX.MainWait"
internal const val DIV_ASYNC_BINDING_MAIN = "$DIV_ASYNC_BINDING_PREFIX.Main"
internal const val DIV_ASYNC_BINDING_TOTAL = "$DIV_ASYNC_BINDING_PREFIX.Total"

const val DIV_CONTEXT_CREATE_HISTOGRAM = "Div.Context.Create"
const val DIV_VIEW_CREATE_HISTOGRAM = "Div.View.Create"
