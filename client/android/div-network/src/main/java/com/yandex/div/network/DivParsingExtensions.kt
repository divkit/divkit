package com.yandex.div.network

import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.histogram.DivParsingHistogramReporter
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivPatch
import org.json.JSONObject

internal fun JSONObject.asDivPatchWithTemplates(
    histogramReporter: DivParsingHistogramReporter,
    errorLogger: ParsingErrorLogger,
): DivPatch {
    val templates = optJSONObject("templates")
    val card = getJSONObject("patch")

    val environment = DivParsingEnvironment(errorLogger)
    templates?.let {
        environment.parseTemplatesWithHistograms(it, histogramReporter)
    }

    return DivPatch(environment, card)
}

private fun DivParsingEnvironment.parseTemplatesWithHistograms(
    templates: JSONObject,
    histogramReporter: DivParsingHistogramReporter,
) {
    histogramReporter.measureTemplatesParsing(templates, null) {
        parseTemplates(templates)
    }
}
