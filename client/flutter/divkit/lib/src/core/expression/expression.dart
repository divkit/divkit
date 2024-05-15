import 'package:divkit/src/core/expression/resolver.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/core/expression/analyzer.dart';
import 'package:equatable/equatable.dart';

abstract class Expression<T> with EquatableMixin {
  T? get value;

  const Expression();

  Future<T> resolveValue({
    required DivVariableContext context,
  });
}

class ValueExpression<T> extends Expression<T> {
  @override
  final T? value;

  const ValueExpression(this.value);

  @override
  Future<T> resolveValue({
    required DivVariableContext context,
  }) async =>
      value!;

  @override
  List<Object?> get props => [value];
}

class ResolvableExpression<T> extends Expression<T> {
  final String? source;

  final Set<String>? variables;

  final T? Function(Object?)? parse;

  final T? fallback;

  @override
  T? value;

  ResolvableExpression(
    this.source, {
    this.parse,
    this.fallback,
  }) : variables = exprAnalyzer.extractVariables(source!);

  @override
  Future<T> resolveValue({
    required DivVariableContext context,
  }) async {
    value = await exprResolver.resolve(this, context: context);

    return value!;
  }

  @override
  List<Object?> get props => [value, source, fallback];
}
