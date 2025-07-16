package com.yandex.divkit.demo.settings

import com.yandex.div.core.experiments.Experiment

/**
 * To set default experiment value in demo app different from value for external clients.
 */
internal val experimentDefaultOverride: Map<Experiment, Boolean> = mapOf(
    Experiment.RENDER_EFFECT_ENABLED to true,
)
