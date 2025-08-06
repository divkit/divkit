package com.yandex.div.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UCallableReferenceExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.UParameter
import org.jetbrains.uast.getParameterForArgument
import org.jetbrains.uast.getParentOfType
import org.jetbrains.uast.getQualifiedName
import org.jetbrains.uast.toUElementOfType
import org.jetbrains.uast.util.isConstructorCall

class OnPreDrawListenerDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
        UClass::class.java,
        ULambdaExpression::class.java,
        UCallableReferenceExpression::class.java
    )

    override fun createUastHandler(context: JavaContext): UElementHandler = OnPreDrawListenerVisitor(context)
}

class OnPreDrawListenerVisitor(
    private val context: JavaContext
) : UElementHandler() {

    override fun visitClass(node: UClass) {
        val usage = node.uastSuperTypes.find { typeReference ->
            typeReference.getQualifiedName() == TARGET_INTERFACE_FQN
        }
        if (usage != null) {
            reportIssue(scopeClass = node, usage = usage)
        }
    }

    override fun visitLambdaExpression(node: ULambdaExpression) {
        val callExpression = node.uastParent as? UCallExpression ?: return
        val constructorExpression = if (callExpression.isConstructorCall()) {
            callExpression.classReference
        } else {
            null
        }
        val lambdaType = node.functionalInterfaceType?.canonicalText

        if (lambdaType == TARGET_INTERFACE_FQN) {
            val nodeClass = node.getParentOfType<UClass>()

            val usageNode = if (constructorExpression.getQualifiedName() == TARGET_INTERFACE_FQN) {
                constructorExpression!!
            } else {
                node
            }

            val location = if (constructorExpression.getQualifiedName() == TARGET_INTERFACE_FQN) {
                context.getCallLocation(
                    callExpression,
                    includeReceiver = true,
                    includeArguments = false
                )
            } else {
                context.getLocation(node)
            }

            reportIssue(
                scopeClass = nodeClass,
                usage = usageNode,
                location = location
            )
        }
    }

    override fun visitCallableReferenceExpression(node: UCallableReferenceExpression) {
        val callExpression = node.uastParent as? UCallExpression ?: return
        val referenceParameter = callExpression.getParameterForArgument(node).toUElementOfType<UParameter>() ?: return
        val referenceType = referenceParameter.typeReference?.getQualifiedName()

        if (referenceType == TARGET_INTERFACE_FQN) {
            val nodeClass = node.getParentOfType<UClass>()
            reportIssue(
                scopeClass = nodeClass,
                usage = node
            )
        }
    }

    private fun reportIssue(
        scopeClass: UClass?,
        usage: UElement,
        location: Location = context.getLocation(usage)
    ) {
        context.report(
            issue = OnPreDrawListenerIssue.get(),
            scopeClass = scopeClass,
            location = location,
            message = OnPreDrawListenerIssue.fullDescription()
        )
    }

    private companion object {
        const val TARGET_INTERFACE_FQN = "android.view.ViewTreeObserver.OnPreDrawListener"
    }
}
