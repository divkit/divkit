package divkit.convention

plugins {
    id("me.tylerbwong.gradle.metalava")
}

metalava {
    version = "1.0.0-alpha14"
    filename = "api/${project.name}.txt"
    apiCompatAnnotations = listOf("androidx.compose.runtime.Composable")
    hiddenAnnotations = listOf(
        "com.yandex.div.core.annotations.ExperimentalApi",
        "com.yandex.div.core.annotations.InternalApi",
        "com.yandex.yatagan.internal.YataganGenerated",
    )
    arguments = setOf(
        "--hide=DeprecationMismatch",
        "--hide=HiddenSuperclass",
        "--hide=HiddenTypeParameter",
        "--hide=ReferencesHidden",
        "--hide=IoError",
        "--hide=UnavailableSymbol",
    )
}
