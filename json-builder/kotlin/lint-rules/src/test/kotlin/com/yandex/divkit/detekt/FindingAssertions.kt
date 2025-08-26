package com.yandex.divkit.detekt

import io.gitlab.arturbosch.detekt.test.FindingAssert
import org.junit.jupiter.api.Assertions.assertEquals

fun FindingAssert.hasSignature(expected: String): FindingAssert {
    val finding = actual ?: return this
    val element = finding.entity.ktElement?.text
    assertEquals(expected, element)
    return this
}
