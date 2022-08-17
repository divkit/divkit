package com.yandex.div.evaluable.multiplatform

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.function.BuiltinFunctionProvider
import com.yandex.div.evaluable.multiplatform.MultiplatformTestUtils.isForAndroidPlatform
import com.yandex.div.evaluable.multiplatform.MultiplatformTestUtils.parsePlatform
import com.yandex.div.evaluable.multiplatform.MultiplatformTestUtils.toListOfJSONObject
import org.json.JSONException
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.lang.RuntimeException

@RunWith(Parameterized::class)
class SignaturesMultiplatformTest(caseOrError: TestCaseOrError<SignatureTestCase>) {
    private val functionProvider = BuiltinFunctionProvider()
    private val signature = caseOrError.getCaseOrThrow()

    @Test
    fun runSignatureTests() {
        try {
            functionProvider.ensureFunctionRegistered(
                signature.functionName,
                signature.arguments,
                signature.resultType
            )
        } catch (e: EvaluableException) {
            throw RuntimeException("Test for signature \"$signature\" failed.", e)
        }
    }

    data class SignatureTestCase(
        val name: String,
        val functionName: String,
        val arguments: List<FunctionArgument>,
        val resultType: EvaluableType,
    ) {
        override fun toString(): String {
            return name
        }
    }

    companion object {
        private const val SIGNATURES_FILE_PATH = "expression_test_data"

        private const val SIGNATURE_FIELD = "signatures"
        private const val SIGNATURE_NAME_FIELD = "name"
        private const val SIGNATURE_FUNCTION_NAME = "function_name"
        private const val SIGNATURE_ARGUMENTS_FIELD = "arguments"
        private const val SIGNATURE_RESULT_TYPE_FIELD = "result_type"
        private const val ARGUMENT_TYPE_FIELD = "type"
        private const val ARGUMENT_VARARG_FIELD = "vararg"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun signatures(): List<TestCaseOrError<SignatureTestCase>> {
            val cases = mutableListOf<TestCaseOrError<SignatureTestCase>>()
            val errors = MultiplatformTestUtils.walkJSONs(SIGNATURES_FILE_PATH) { file, json ->
                val newCases = json.optJSONArray(SIGNATURE_FIELD).toListOfJSONObject()
                    .filter { isForAndroidPlatform(parsePlatform(it)) }
                    .map { parseSignature(file, it) }
                    .filter { it.error != null }
                cases.addAll(newCases)

            }
            return errors.map { TestCaseOrError<SignatureTestCase>(it) } + cases
        }

        private fun parseSignature(file: File, json: JSONObject): TestCaseOrError<SignatureTestCase> {
            val name = try {
                json.getString(SIGNATURE_NAME_FIELD)
            } catch (e: JSONException) {
                return TestCaseOrError(TestCaseParsingError("???", file, json, e))
            }

            try {
                return TestCaseOrError(SignatureTestCase(
                    name,
                    json.getString(SIGNATURE_FUNCTION_NAME),
                    json.optJSONArray(SIGNATURE_ARGUMENTS_FIELD)?.let { array ->
                        val result = mutableListOf<FunctionArgument>()
                        for (i in 0 until array.length()) {
                            val argument = array.getJSONObject(i)
                            val type = EvaluableType.valueOf(
                                argument.getString(ARGUMENT_TYPE_FIELD).uppercase()
                            )
                            val vararg = argument.optBoolean(ARGUMENT_VARARG_FIELD)
                            result.add(FunctionArgument(type, vararg))
                        }
                        result
                    } ?: emptyList(),
                    EvaluableType.valueOf(json.getString(SIGNATURE_RESULT_TYPE_FIELD).uppercase()),
                ))

            } catch (e: JSONException) {
                return TestCaseOrError(TestCaseParsingError(name, file, json, e))
            }
        }
    }
}
