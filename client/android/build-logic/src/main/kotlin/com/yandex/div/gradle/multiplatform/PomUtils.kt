package com.yandex.div.gradle.multiplatform

import com.yandex.div.gradle.appendElement
import com.yandex.div.gradle.find
import org.gradle.api.XmlProvider

fun configureDefaultKmpDependencies(xml: XmlProvider, platformId: PlatformIdentifier) {
    val xmlElement = xml.asElement()
    val groupId = xmlElement.find { it.nodeName == "groupId" }?.textContent
        ?: throw IllegalArgumentException("Failed to locate groupId node")
    val artifactId = xmlElement.find { it.nodeName == "artifactId" }?.textContent
        ?: throw IllegalArgumentException("Failed to locate artifactId node")
    val version = xmlElement.find { it.nodeName == "version" }?.textContent
        ?: throw IllegalArgumentException("Failed to locate version node")

    val dependencies = xmlElement.find { it.nodeName == "dependencies" }
        ?: xmlElement.appendElement("dependencies")
    dependencies.appendElement("dependency").apply {
        appendElement("groupId", groupId)
        appendElement("artifactId", "$artifactId-${platformId.id}")
        appendElement("version", version)
        if (platformId == PlatformIdentifier.ANDROID) {
            appendElement("type", "aar")
        }
        appendElement("scope", "compile")
    }
}
