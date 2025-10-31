package com.yandex.div.evaluable.multiplatform

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.function.BuiltinFunctionProvider
import com.yandex.div.test.expression.MultiplatformTestUtils
import com.yandex.div.test.expression.MultiplatformTestUtils.isForAndroidPlatform
import com.yandex.div.test.expression.MultiplatformTestUtils.parsePlatform
import com.yandex.div.test.expression.MultiplatformTestUtils.toListOfJSONObject
import com.yandex.div.test.expression.TestCaseOrError
import com.yandex.div.test.expression.TestCaseParsingError
import org.json.JSONException
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class SignaturesMultiplatformTest(caseOrError: TestCaseOrError<SignatureTestCase>) {
    private val functionProvider = BuiltinFunctionProvider
    private val signature = caseOrError.getCaseOrThrow()

    @Test
    fun runSignatureTests() {
        try {
            functionProvider.ensureFunctionRegistered(
                signature.functionName,
                signature.arguments,
                signature.returnType,
                signature.isMethod
            )
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
        fun signatures(): List<TestCaseOrError<SignatureTestCase>> {
            val cases = mutableListOf<TestCaseOrError<SignatureTestCase>>()
            val errors = MultiplatformTestUtils.walkJSONs(File(SIGNATURES_DIR_PATH)) { file, jsonString ->
                val newCases = JSONObject(jsonString).optJSONArray(FIELD_SIGNATURE).toListOfJSONObject()
                    .filter { isForAndroidPlatform(parsePlatform(it)) }
                    .map { parseSignature(file, it) }
                    .filter { it.error == null }
                cases.addAll(newCases)

            }
            return errors.map { TestCaseOrError<SignatureTestCase>(it) } + cases
        }

        private fun parseSignature(file: File, json: JSONObject): TestCaseOrError<SignatureTestCase> {
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
                return TestCaseOrError(TestCaseParsingError(file.name, json, e))
            }
            val isMethod = json.optBoolean(FIELD_SIGNATURE_IS_METHOD)
            return TestCaseOrError(SignatureTestCase(
                "$functionName(${arguments ?: ""}) $resultType",
                functionName,
                arguments ?: emptyList(),
                resultType,
                isMethod)
            )
        }
    }
}
