package divkit.convention

import com.yandex.div.gradle.ValidateTestResultsTask

plugins {
    id("com.android.application")
}

val validateTestResults = ValidateTestResultsTask.register(project)

androidComponents.onVariants { variant ->
    tasks.matching {
        it.name == variant.computeTaskName("connected", "androidTest")
    }.configureEach {
        finalizedBy(validateTestResults)
    }
}
