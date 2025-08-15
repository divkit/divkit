package com.yandex.div.core.dagger

import com.yandex.div.core.experiments.Experiment
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ExperimentFlag(val experiment: Experiment)
