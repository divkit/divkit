@file:JvmName("Main")

package divkit.dsl.expression.generator

import java.io.File

fun main(args: Array<String>) {
    val outputDir = File(args[0])
    val inputDir = File(args[1])
    val packageName = args[2]
    outputDir.mkdirs()
    if (!outputDir.exists()) {
        throw RuntimeException("Failed to create directories in $outputDir")
    }
    DivkitFunctionsGenerator(outputDir, inputDir, packageName).generate()
}
