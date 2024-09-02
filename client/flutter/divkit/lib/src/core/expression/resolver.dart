import 'package:div_expressions_resolver/div_expressions_resolver.dart';
import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/core/protocol/div_logger.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:flutter/services.dart';

/// Print logs with information about expression calculations.
bool debugPrintDivExpressionResolve = false;

void _log(String message) {
  if (debugPrintDivExpressionResolve) {
    loggerUse(const DefaultDivLoggerContext('onResolve')).debug(message);
  }
}

final exprResolver = DefaultDivExpressionResolver();

abstract class DivExpressionResolver {
  /// Parses the source code of the expression from [source]
  /// and returns a the result of execution.
  ///
  /// Parameters:
  /// - [source] - source code of the expression.
  /// - [context] - used to specify the variables used and their values.
  Future<T> resolve<T>(
    Expression<T> expression, {
    required DivVariableContext context,
  });

  /// Re-create new DivVariableController to clear variables.
  /// Helps to fix variable mutation exception.
  Future<void> clearVariables();
}

class DefaultDivExpressionResolver implements DivExpressionResolver {
  final _platform = NativeDivExpressionsResolver();

  @override
  Future<T> resolve<T>(
    Expression<T> expression, {
    required DivVariableContext context,
  }) async {
    if (expression is ResolvableExpression) {
      final expr = expression as ResolvableExpression;
      try {
        var result = await _platform.resolve(
          expr.source!,
          context: context.current,
        );

        if (expr.parse != null) {
          result = expr.parse!(result);
        }

        _log(
          "Expr ${expr.source} with args ${context.current}"
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

  @override
  Future<void> clearVariables() async {
    try {
      await _platform.clearVariables();
    } on PlatformException catch (e, st) {
      logger.error(e.message, stackTrace: st);
    }
  }
}
