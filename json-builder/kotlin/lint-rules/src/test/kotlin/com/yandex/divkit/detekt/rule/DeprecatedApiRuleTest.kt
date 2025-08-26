package com.yandex.divkit.detekt.rule

import com.yandex.divkit.detekt.hasSignature
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class DeprecatedApiRuleTest(private val env: KotlinCoreEnvironment) {

    private val annotation = """
        package divkit.dsl.annotation
        
        import kotlin.annotation.AnnotationTarget.CLASS
        import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
        import kotlin.annotation.AnnotationTarget.FUNCTION
        import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
        
        @Target(CLASS, FUNCTION, CONSTRUCTOR, VALUE_PARAMETER)
        @Retention(AnnotationRetention.SOURCE)
        annotation class DeprecatedApi
    """

    private val deprecatedClass = """
        package test
        
        import divkit.dsl.annotation.DeprecatedApi
        
        @DeprecatedApi 
        open class DeprecatedModel(
            val value: String
        )
    """

    private val deprecatedConstructorParameter = """
        package test
        
        import divkit.dsl.annotation.DeprecatedApi
        
        class Model(
            val value: String,
            @DeprecatedApi val deprecatedValue: String = ""
        )
    """

    private val deprecatedFunction = """
        package test
        
        import divkit.dsl.annotation.DeprecatedApi
        
        class Model(
            val value: String
        )
        
        @DeprecatedApi
        fun deprecatedModel(value: String): Model {
            return Model(value)
        }
    """

    private val deprecatedFunctionParameter = """
        package test
        
        import divkit.dsl.annotation.DeprecatedApi
        
        class Model(
            val value: String,
            val deprecatedValue: String
        )
        
        fun model(
            value: String,
            @DeprecatedApi deprecatedValue: String = ""
        ): Model {
            return Model(value, deprecatedValue)
        }
    """

    private val deprecatedApiRule = DeprecatedApiRule(Config.empty)

    @Test
    fun `deprecated class import detected`() {
        val usage = """
            package test
            
            import test.DeprecatedModel
            
            val model = DeprecatedModel(value = "Hello World")
        """.trimIndent()

        val findings = deprecatedApiRule.lintWithContext(env, usage, annotation, deprecatedClass)

        assertThat(findings).hasSize(2)
        assertThat(findings.first())
            .hasSourceLocation(line = 3, column = 8)
            .hasSignature("test.DeprecatedModel")
            .hasMessage("DeprecatedModel is deprecated.")
    }

    @Test
    fun `deprecated class name detected`() {
        val usage = """
            package test
            
            val modelClass: DeprecatedModel? = null
        """.trimIndent()

        val findings = deprecatedApiRule.lintWithContext(env, usage, annotation, deprecatedClass)

        assertThat(findings).hasSize(1)
        assertThat(findings.first())
            .hasSourceLocation(line = 3, column = 17)
            .hasSignature("DeprecatedModel?")
            .hasMessage("DeprecatedModel is deprecated.")
    }

    @Test
    fun `deprecated class fully qualified name detected`() {
        val usage = """
            package test
            
            val modelClass = test.DeprecatedModel::class
        """.trimIndent()

        val findings = deprecatedApiRule.lintWithContext(env, usage, annotation, deprecatedClass)

        assertThat(findings).hasSize(1)
        assertThat(findings.first())
            .hasSourceLocation(line = 3, column = 18)
            .hasSignature("test.DeprecatedModel")
            .hasMessage("DeprecatedModel is deprecated.")
    }

    @Test
    fun `deprecated class instance detected`() {
        val usage = """
            package test
            
            val model = DeprecatedModel(value = "Hello World")
        """.trimIndent()

        val findings = deprecatedApiRule.lintWithContext(env, usage, annotation, deprecatedClass)

        assertThat(findings).hasSize(1)
        assertThat(findings.first())
            .hasSourceLocation(line = 3, column = 13)
            .hasSignature("DeprecatedModel")
            .hasMessage("DeprecatedModel is deprecated.")
    }

    @Test
    fun `deprecated class inheritance detected`() {
        val usage = """
            package test
            
            class Model(value: String) : DeprecatedModel(value)
        """.trimIndent()

        val findings = deprecatedApiRule.lintWithContext(env, usage, annotation, deprecatedClass)

        assertThat(findings).hasSize(2)
        assertThat(findings.first())
            .hasSourceLocation(line = 3, column = 30)
            .hasSignature("DeprecatedModel")
            .hasMessage("DeprecatedModel is deprecated.")
    }

    @Test
    fun `deprecated named constructor argument detected`() {
        val usage = """
            package test
            
            val model = Model(value = "Hello", deprecatedValue = "World")
        """.trimIndent()

        val findings = deprecatedApiRule.lintWithContext(env, usage, annotation, deprecatedConstructorParameter)

        assertThat(findings).hasSize(1)
        assertThat(findings.first())
            .hasSourceLocation(line = 3, column = 36)
            .hasSignature("deprecatedValue = \"World\"")
            .hasMessage("deprecatedValue is deprecated.")
    }

    @Test
    fun `deprecated constructor argument detected`() {
        val usage = """
            package test
            
            val model = Model("Hello", "World")
        """.trimIndent()

        val findings = deprecatedApiRule.lintWithContext(env, usage, annotation, deprecatedConstructorParameter)

        assertThat(findings).hasSize(1)
        assertThat(findings.first())
            .hasSourceLocation(line = 3, column = 28)
            .hasSignature("\"World\"")
            .hasMessage("deprecatedValue is deprecated.")
    }

    @Test
    fun `deprecated function import detected`() {
        val usage = """
            package test
            
            import test.deprecatedModel
            
            val model = deprecatedModel(value = "Hello World")
        """.trimIndent()

        val findings = deprecatedApiRule.lintWithContext(env, usage, annotation, deprecatedFunction)

        assertThat(findings).hasSize(2)
        assertThat(findings.first())
            .hasSourceLocation(line = 3, column = 8)
            .hasSignature("test.deprecatedModel")
            .hasMessage("deprecatedModel is deprecated.")
    }

    @Test
    fun `deprecated function detected`() {
        val usage = """
            package test
            
            val model = deprecatedModel(value = "Hello World")
        """.trimIndent()

        val findings = deprecatedApiRule.lintWithContext(env, usage, annotation, deprecatedFunction)

        assertThat(findings).hasSize(1)
        assertThat(findings.first())
            .hasSourceLocation(line = 3, column = 13)
            .hasSignature("deprecatedModel")
            .hasMessage("deprecatedModel is deprecated.")
    }

    @Test
    fun `deprecated named function parameter detected`() {
        val usage = """
            package test
            
            val model = model(value = "Hello", deprecatedValue = "World")
        """.trimIndent()

        val findings = deprecatedApiRule.lintWithContext(env, usage, annotation, deprecatedFunctionParameter)

        assertThat(findings).hasSize(1)
        assertThat(findings.first())
            .hasSourceLocation(line = 3, column = 36)
            .hasSignature("deprecatedValue = \"World\"")
            .hasMessage("deprecatedValue is deprecated.")
    }

    @Test
    fun `deprecated function parameter detected`() {
        val usage = """
            package test
            
            val model = model("Hello", "World")
        """.trimIndent()

        val findings = deprecatedApiRule.lintWithContext(env, usage, annotation, deprecatedFunctionParameter)

        assertThat(findings).hasSize(1)
        assertThat(findings.first())
            .hasSourceLocation(line = 3, column = 28)
            .hasSignature("\"World\"")
            .hasMessage("deprecatedValue is deprecated.")
    }
}
