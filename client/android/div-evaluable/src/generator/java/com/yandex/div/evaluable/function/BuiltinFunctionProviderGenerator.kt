package com.yandex.div.evaluable.function

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.FunctionProvider
import java.io.File

object BuiltinFunctionProviderGenerator {
    private val nameAndFunctions = BuiltinFunctionProvider.exposedFunctions.entries
        .map { (name, functions) ->
            name to functions.map { it::class.asClassName() }.sorted()
        }
        .sortedBy { (id, _) -> id }
    private val nameAndMethods = BuiltinFunctionProvider.exposedMethods.entries
        .map { (name, functions) ->
            name to functions.map { it::class.asClassName() }.sorted()
        }
        .sortedBy { (id, _) -> id }

    @JvmStatic
    fun main(args: Array<String>) {
        val outputFolder = File("")
        val generatedFile = prepareGeneratedBuiltinFunctionProviderFile()

        generatedFile.writeTo(outputFolder)
    }

    private fun prepareGeneratedBuiltinFunctionProviderFile(): FileSpec =
        FileSpec.builder(
            packageName = "com.yandex.div.evaluable.function",
            fileName = "GeneratedBuiltinFunctionProvider"
        )
            .addType(prepareTypeSpec())
            .build()

    private fun prepareTypeSpec(): TypeSpec =
        TypeSpec.objectBuilder("GeneratedBuiltinFunctionProvider")
            .addSuperinterface(FunctionProvider::class)
            .addFunctions(listOf(
                prepareGetFunction(nameAndFunctions, SignatureType.Function),
                prepareGetFunction(nameAndMethods, SignatureType.Method),
                prepareWarmUpFunction()
            ))
            .build()

    private fun prepareGetFunction(
        namesAndFunctions: List<Pair<String, List<ClassName>>>,
        type: SignatureType
    ): FunSpec {
        val nameParameterSpec = ParameterSpec.builder("name", String::class).build()
        val argsParameterSpec = ParameterSpec.builder(
            "args",
            List::class.parameterizedBy(EvaluableType::class)
        ).build()

        val code = CodeBlock.builder()

        code.beginControlFlow("when (%N)", nameParameterSpec).apply {
            val addSingleFunction = { name: String, function: ClassName ->
                beginControlFlow("%S ->", name).apply {
                    addStatement(
                        "return %T.withArgumentsValidation(%N)",
                        function,
                        argsParameterSpec
                    )
                }.endControlFlow()
            }

            val addMultipleFunctions = { name: String, functions: List<ClassName> ->
                beginControlFlow("%S ->", name).apply {
                    functions.forEach { function ->
                        beginControlFlow(
                            "if (%T.matchesArguments(%N) == %T)",
                            function,
                            argsParameterSpec,
                            Function.MatchResult.Ok::class
                        )
                        addStatement("return %T", function)
                        endControlFlow()
                    }
                    functions.forEach { function ->
                        beginControlFlow(
                            "if (%T.matchesArgumentsWithCast(%N) == %T)",
                            function,
                            argsParameterSpec,
                            Function.MatchResult.Ok::class
                        )
                        addStatement("return %T", function)
                        endControlFlow()
                    }
                    val capitalizedTypeDescription = type.description.replaceFirstChar { it.titlecase() }
                    code.addStatement(
                        "throw %N(%N, %N)",
                        FunSpec.builder("get${capitalizedTypeDescription}ArgumentsException").build(),
                        nameParameterSpec,
                        argsParameterSpec
                    )
                }.endControlFlow()
            }

            namesAndFunctions.forEach { (name, functions) ->
                functions.singleOrNull()?.let { addSingleFunction(name, it) }
                    ?: addMultipleFunctions(name, functions)
            }
        }.endControlFlow()

        code.addStatement(
            "throw %T(\"Unknown ${type.description} name: \$name.\")",
            EvaluableException::class
        )

        return FunSpec.builder(type.getMethodName)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(nameParameterSpec)
            .addParameter(argsParameterSpec)
            .returns(Function::class)
            .addCode(code.build())
            .build()
    }

    private fun prepareWarmUpFunction(): FunSpec {
        val code = CodeBlock.builder()

        val functions = nameAndFunctions
            .flatMap { (_, functions) -> functions }
            .sorted()

        functions.forEach { function ->
            code.addStatement("%T", function)
        }

        return FunSpec.builder("warmUp")
            .addCode(code.build())
            .build()
    }
}

private enum class SignatureType(
    val description: String,
    val getMethodName: String
) {
    Function("function", "get"),
    Method("method", "getMethod")
}
