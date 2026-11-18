package com.yandex.div.compose.screenshot

import java.io.File

fun File.getRelativeFileName(testDataDir: String): String {
    return relativeTo(File("../../../test_data/$testDataDir")).invariantSeparatorsPath
}
