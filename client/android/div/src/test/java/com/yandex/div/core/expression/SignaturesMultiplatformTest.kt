package com.yandex.div.core.expression

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.test.crossplatform.isForAndroid
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils
import com.yandex.div.test.crossplatform.toObjectList
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class SignaturesMultiplatformTest(testCaseParsingResult: ParsingResult<SignatureTestCase>) {
    private val functionProvider = GeneratedBuiltinFunctionProvider
    private val signature = testCaseParsingResult.getOrThrow()

    @Test
    fun runSignatureTests() {
        try {
            val name = signature.functionName
            val argumentTypes = signature.arguments.map { it.type }
            val function = if (signature.isMethod) {
                functionProvider.getMethod(name, argumentTypes)
            } else {
                functionProvider.get(name, argumentTypes)
            }
            assertEquals(signature.returnType, function.resultType)
        } catch (e: EvaluableException) {
            throw RuntimeException("Test for signature \"$signature\" failed.", e)
        }
    }

    data class SignatureTestCase(
        val name: String,
        val functionName: String,
        val arguments: List<FunctionArgument>,
        val returnType: EvaluableType,
        val isMethod: Boolean,
    ) {
        override fun toString(): String {
            return name
        }
    }

    companion object {
        private const val SIGNATURES_DIR_PATH = "../../../expression-api"

        private const val FIELD_SIGNATURE = "signatures"
        private const val FIELD_SIGNATURE_NAME = "name"
        private const val FIELD_SIGNATURE_ARGUMENTS = "arguments"
        private const val FIELD_SIGNATURE_RETURN_TYPE = "return_type"
        private const val FIELD_SIGNATURE_IS_METHOD = "is_method"
        private const val FIELD_ARGUMENT_TYPE = "type"
        private const val FIELD_ARGUMENT_IS_VARARG = "is_vararg"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun signatures(): List<ParsingResult<SignatureTestCase>> {
            return ParsingUtils.parseFiles(File(SIGNATURES_DIR_PATH)) { file, json ->
                JSONObject(json)
                    .optJSONArray(FIELD_SIGNATURE)
                    .toObjectList()
                    .filter { it.isForAndroid }
                    .map { parseSignature(file, it) }
                    .filterIsInstance<ParsingResult.Success<SignatureTestCase>>()
            }
        }

        private fun parseSignature(file: File, json: JSONObject): ParsingResult<SignatureTestCase> {
            val functionName = json.getString(FIELD_SIGNATURE_NAME)
            val arguments = json.optJSONArray(FIELD_SIGNATURE_ARGUMENTS)?.let { array ->
                val result = mutableListOf<FunctionArgument>()
                for (i in 0 until array.length()) {
                    val argument = array.getJSONObject(i)
                    val type = EvaluableType.valueOf(
                        argument.getString(FIELD_ARGUMENT_TYPE).uppercase()
                    )
                    val vararg = argument.optBoolean(FIELD_ARGUMENT_IS_VARARG)
                    result.add(FunctionArgument(type, vararg))
                }
                result
            }
            val resultType = try {
                EvaluableType.valueOf(json.getString(FIELD_SIGNATURE_RETURN_TYPE).uppercase())
            } catch (e: JSONException) {
                return ParsingResult.Error(file.name, json, e)
            }
            val isMethod = json.optBoolean(FIELD_SIGNATURE_IS_METHOD)
            return ParsingResult.Success(
                SignatureTestCase(
                    "$functionName(${arguments ?: ""}) $resultType",
                    functionName,
                    arguments ?: emptyList(),
                    resultType,
                    isMethod
                )
            )
        }
    }
}
