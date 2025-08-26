package com.yandex.divkit.detekt.rule

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedElementSelector
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getParameterForArgument
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall

@RequiresTypeResolution
class DeprecatedApiRule(config: Config) :Rule(config) {

    override val issue = Issue(
        id = "deprecated-api",
        description = "Deprecated elements should not be used.",
        severity = Severity.Defect,
        debt = Debt.TEN_MINS
    )

    private val deprecationAnnotations = listOf(
        FqName("divkit.dsl.annotation.DeprecatedApi")
    )

    override fun visitImportDirective(importDirective: KtImportDirective) {
        if (!importDirective.isAllUnder) {
            val importedReference = importDirective.importedReference ?: return
            val referenceExpression = importedReference.getQualifiedElementSelector() as? KtReferenceExpression ?: return
            checkReferenceForAnnotations(importedReference, referenceExpression)
        }
    }

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        klass.superTypeListEntries.forEach { superTypeEntry ->
            val typeReference = superTypeEntry.typeReference
            if (typeReference != null) {
                visitTypeReference(typeReference)
            }
        }
    }

    override fun visitTypeReference(typeReference: KtTypeReference) {
        super.visitTypeReference(typeReference)

        val userType = asUserType(typeReference.typeElement)
        val referenceExpression = userType?.referenceExpression ?: return
        checkReferenceForAnnotations(typeReference, referenceExpression)
    }

    private fun asUserType(typeElement: KtTypeElement?): KtUserType? {
        return when (typeElement) {
            is KtNullableType -> asUserType(typeElement.innerType)
            is KtUserType -> typeElement
            else -> null
        }
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        val selectorExpression = expression.selectorExpression
        val referenceExpression = selectorExpression as? KtReferenceExpression ?: return
        checkReferenceForAnnotations(expression, referenceExpression)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (bindingContext == BindingContext.EMPTY) return

        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
        checkCallForAnnotations(expression)
        checkValueArguments(expression, resolvedCall)
    }

    private fun checkCallForAnnotations(callExpression: KtCallExpression) {
        val calleeExpression = callExpression.calleeExpression ?: return
        val referenceExpression = calleeExpression as? KtReferenceExpression ?: return
        checkReferenceForAnnotations(calleeExpression, referenceExpression)
    }

    private fun checkValueArguments(
        callExpression: KtCallExpression,
        resolvedCall: ResolvedCall<*>
    ) {
        callExpression.valueArguments.forEach { valueArgument ->
            val parameterDescriptor = resolvedCall.getParameterForArgument(valueArgument)
            if (parameterDescriptor != null) {
                checkParameterForAnnotations(valueArgument, parameterDescriptor)
            }
        }
    }

    private fun checkReferenceForAnnotations(element: KtElement, reference: KtReferenceExpression) {
        if (bindingContext == BindingContext.EMPTY) return

        val referenceDescriptor = bindingContext[BindingContext.REFERENCE_TARGET, reference] ?: return
        val declarationDescriptor = referenceDescriptor as? DeclarationDescriptor ?: return
        checkElementForAnnotations(element, reference.text, declarationDescriptor)

        if (referenceDescriptor is ConstructorDescriptor) {
            checkElementForAnnotations(element, reference.text, referenceDescriptor.constructedClass)
        }
    }

    private fun checkParameterForAnnotations(
        valueArgument: KtValueArgument,
        parameterDescriptor: ValueParameterDescriptor
    ) = checkElementForAnnotations(valueArgument, parameterDescriptor.name.asString(), parameterDescriptor)

    private fun checkElementForAnnotations(
        element: KtElement,
        elementDescription: String,
        descriptor: DeclarationDescriptor
    ) {
        val matchingAnnotations = descriptor.annotations
            .mapNotNull { annotation -> findDeprecationAnnotation(annotation) }

        if (matchingAnnotations.isNotEmpty()) {
            val entity = if (element is KtNamedDeclaration) {
                Entity.atName(element)
            } else {
                Entity.from(element)
            }

            report(
                CodeSmell(
                    issue,
                    entity,
                    "$elementDescription is deprecated."
                )
            )
        }
    }

    private fun findDeprecationAnnotation(annotation: AnnotationDescriptor): String? {
        val annotationFqName = annotation.fqName ?: return null
        return if (annotationFqName in deprecationAnnotations) {
            annotationFqName.shortName().asString()
        } else {
            null
        }
    }
}
