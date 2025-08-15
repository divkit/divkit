import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/expression/analyzer.dart';
import 'package:divkit/src/core/runtime/entities.dart';
import 'package:divkit/src/utils/trace.dart';
import 'package:equatable/equatable.dart';
import 'package:petitparser/petitparser.dart';

abstract class Resolvable<T> extends Object {
  const Resolvable();

  T resolve(DivVariableContext context);
}

abstract class Expression<T> extends Resolvable<T> with EquatableMixin {
  T get value;

  const Expression();

  @override
  T resolve(DivVariableContext context);
}

class ValueExpression<T> extends Expression<T> {
  @override
  final T value;

  const ValueExpression(this.value);

  @override
  T resolve(DivVariableContext context) => value;

  @override
  List<Object?> get props => [value];
}

class ResolvableExpression<T> extends Expression<T> {
  final String? source;

  Result<dynamic>? _executionTree;

  ExpressionToken get executionTree {
    if (_executionTree! is Success) {
      return _executionTree!.value;
    }
    throw _executionTree!.message;
  }

  Set<String>? variables;

  final T? Function(Object?)? parse;

  final T? fallback;

  T? _value;

  @override
  T get value => _value!;

  ResolvableExpression(
    this.source, {
    this.parse,
    this.fallback,
  });

  @override
  T resolve(DivVariableContext context) {
    _executionTree ??= parser.parse(source!);
    variables ??= traceFunc(
      'extractVariables',
      () => exprAnalyzer.extractVariables(source!),
    );

    final hasValue = _value != null;
    final hasUpdate = variables!.intersection(context.update).isNotEmpty;
    if (hasUpdate || !hasValue) {
      _value = exprResolver.resolve(this, context: context);
    }
    return value;
  }

  @override
  List<Object?> get props => [source, fallback];
}
