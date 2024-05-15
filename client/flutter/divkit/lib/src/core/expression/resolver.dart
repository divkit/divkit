import 'package:div_expressions_resolver/div_expressions_resolver.dart';
import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/core/protocol/div_logger.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:flutter/services.dart';

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

      final hasValue = expr.value != null;

      final hasUpdate =
          expr.variables?.intersection(context.update).isNotEmpty ?? false;

      if (hasUpdate || !hasValue) {
        try {
          var result = await _platform.resolve(
            expr.source!,
            context: context.current,
          );

          if (expr.parse != null) {
            result = expr.parse!(result);
          }

          logger.debug(
            "Expr ${expr.source} with args ${context.current}"
            "and result $result [${result.runtimeType}]",
          );

          return result as T;
        } on Exception {
          // Try to apply a fallback
          if (expr.fallback != null) {
            return expr.fallback!;
          }

          rethrow;
        }
      }
    }

    if (expression.value == null) {
      throw Exception('The calculation ended with an unhandled error');
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
