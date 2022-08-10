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

internal const val DIV_PARSING_DATA = "$DIV_PARSING_PREFIX.Data"
internal const val DIV_PARSING_TEMPLATES = "$DIV_PARSING_PREFIX.Templates"
internal const val DIV_PARSING_JSON = "$DIV_PARSING_PREFIX.JSON"

internal const val DIV_BINDING_HISTOGRAM = "Div.Binding"
internal const val DIV_REBINDING_HISTOGRAM = "Div.Rebinding"

const val DIV_CONTEXT_CREATE_HISTOGRAM = "Div.Context.Create"
const val DIV_VIEW_CREATE_HISTOGRAM = "Div.View.Create"
