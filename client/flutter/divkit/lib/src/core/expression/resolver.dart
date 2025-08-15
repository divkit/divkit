import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/trace.dart';

/// Print logs with information about expression calculations.
bool debugPrintDivExpressionResolve = false;

void _log(String message) {
  if (debugPrintDivExpressionResolve) {
    loggerUse(const DefaultDivLoggerContext('onResolve')).debug(message);
  }
}

final exprResolver = DefaultDivExpressionResolver();

abstract class DivExpressionResolver {
  /// Calculates the value of an expression based on data from the context.
  T resolve<T>(
    Expression<T> expression, {
    required DivVariableContext context,
  });
}

class DefaultDivExpressionResolver implements DivExpressionResolver {
  @override
  T resolve<T>(
    Expression<T> expression, {
    required DivVariableContext context,
  }) {
    if (expression is ResolvableExpression) {
      final expr = expression as ResolvableExpression;
      try {
        var result = traceFunc(
          'Runtime.execute',
          () => runtime.execute(
            expr.executionTree,
            context.current,
          ),
        );

        if (expr.parse != null) {
          result = expr.parse!(result);
        }

        _log(
          "Expr ${expr.source} with args ${context.current}\n"
          "and result $result [${result.runtimeType}]",
        );

        return result as T;
      } catch (e, st) {
        // Try to apply a fallback
        if (expr.fallback != null) {
          logger.warning(
            "Expr ${expr.source} with fallback ${expr.fallback}",
            error: e,
          );
          return expr.fallback!;
        }
        logger.error("Expr ${expr.source}", error: e, stackTrace: st);
        rethrow;
      }
    }

    if (expression.value == null) {
      final error = Exception('The calculation ended with an unhandled error');
      logger.error("Fatal", error: error);
      throw error;
    }

    return expression.value!;
  }
}
