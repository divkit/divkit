import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/expression/analyzer.dart';
import 'package:divkit/src/utils/trace.dart';
import 'package:equatable/equatable.dart';

abstract class Expression<T> with EquatableMixin {
  T? get value;

  T get requireValue => value!;

  const Expression();

  Future<T> resolveValue({
    required DivVariableContext context,
  });

  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    if (this is ResolvableExpression) {
      try {
        await resolveValue(
          context: DivVariableContext(current: context),
        );
      } catch (e, st) {
        logger.warning(
          '${(this as ResolvableExpression).source} not preloaded via error',
          error: e,
          stackTrace: st,
        );
      }
    }
  }
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

abstract class Preloadable extends Object {
  const Preloadable();

  Future<void> preload(Map<String, dynamic> context);
}

class ResolvableExpression<T> extends Expression<T> {
  final String? source;

  Set<String>? variables;

  final T? Function(Object?)? parse;

  final T? fallback;

  @override
  T? value;

  ResolvableExpression(
    this.source, {
    this.parse,
    this.fallback,
  });

  @override
  Future<T> resolveValue({
    required DivVariableContext context,
  }) async {
    variables ??= await traceAsyncFunc(
      'extractVariables',
      () => exprAnalyzer.extractVariables(source!),
    );

    final hasValue = value != null;
    final hasUpdate = variables!.intersection(context.update).isNotEmpty;
    if (hasUpdate || !hasValue) {
      value = await traceAsyncFunc(
        'resolveExpression',
        () => exprResolver.resolve(this, context: context),
      );
    }
    return value!;
  }

  @override
  List<Object?> get props => [value, source, fallback];
}
