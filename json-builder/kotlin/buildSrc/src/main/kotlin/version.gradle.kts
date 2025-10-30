package divkit.convention

import com.yandex.divkit.gradle.PublicationType
import com.yandex.divkit.gradle.Version
import com.yandex.divkit.gradle.optStringProperty

val versionFile = rootProject.layout.projectDirectory.file("../../version")
val version = providers.fileContents(versionFile).asText.get()
val (major, minor, patch) = version.trim()
    .split('.')
    .map(Integer::parseInt)

val publicationType by extra(PublicationType.fromString(project.optStringProperty("publicationType")))
val divkitVersion by extra(Version(project, major, minor, patch))
val divkitVersionSuffixed by extra("${divkitVersion.versionName}${publicationType.getVersionSuffix()}")
