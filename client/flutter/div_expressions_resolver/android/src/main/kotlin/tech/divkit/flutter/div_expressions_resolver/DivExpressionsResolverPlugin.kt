package tech.divkit.flutter.div_expressions_resolver

import io.flutter.embedding.engine.plugins.FlutterPlugin
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.types.*
import NativeDivExpressionsResolver
import java.lang.Exception

class DivExpressionsResolverPlugin: FlutterPlugin, NativeDivExpressionsResolver {
  private var resolver = ExpressionResolver()
  
  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    NativeDivExpressionsResolver.setUp(flutterPluginBinding.binaryMessenger, this)
  }

  override fun resolve(
    expression: String,
    context: Map<String, Any>,
    callback: (Result<Any?>) -> Unit
  ) {
    try {
      val result = resolver.resolve(expression, context)
      callback(Result.success(result))
    } catch (e: Exception) {
      callback(Result.failure(e))
    }
  }

  override fun clearVariables(callback: (Result<Unit>) -> Unit) {
    resolver.clear()
    callback(Result.success(Unit))
  }
  
  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    NativeDivExpressionsResolver.setUp(binding.binaryMessenger, null)
  }
}
