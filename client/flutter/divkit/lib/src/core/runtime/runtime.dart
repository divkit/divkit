import 'package:divkit/src/core/runtime/core.dart';
import 'package:divkit/src/core/runtime/entities.dart';
import 'package:divkit/src/core/runtime/functions/functions.dart';
import 'package:divkit/src/core/runtime/members.dart';
import 'package:divkit/src/core/runtime/runtime_sync.dart';

const runtime = SyncRuntime(
  functions: defaultFunctions,
  members: defaultMembers,
);

/// Abstraction of execution mechanism of the input execution tree.
abstract class Runtime {
  const Runtime();

  /// Entry point of execution data in the processing function.
  @pragma('vm:prefer-inline')
  dynamic execute(
    ExpressionToken expression, [
    Map<String, dynamic> context = const {},
  ]) {
    try {
      return expression.map(
        composition: (expression) => composition(expression, context),
        condition: (expression) => condition(expression, context),
        guard: (expression) => guard(expression, context),
        unaryPrefix: (expression) => unaryPrefix(expression, context),
        binary: (expression) => binary(expression, context),
        logical: (expression) => logical(expression, context),
        member: (expression) => member(expression, context),
        function: (expression) => function(expression, context),
        literal: (expression) => literal(expression, context),
        reference: (expression) => reference(expression, context),
      );
    } on RangeError catch (e) {
      throw 'Failed to evaluate [$expression]. Requested index (${e.invalidValue}) out of bounds array size (${e.start})';
    } catch (e) {
      if (e is String && e.startsWith('Failed to evaluate')) {
        rethrow;
      }
      throw 'Failed to evaluate [$expression]. $e';
    }
  }

  /// It is responsible for processing embedded expressions into
  /// strings, other expressions, and simple expressions.
  @pragma('vm:prefer-inline')
  dynamic composition(
    CompositionToken expression, [
    Map<String, dynamic> context = const {},
  ]) {
    if (expression.quasis.length == 2 &&
        (expression.quasis[0] as StringToken).value == '' &&
        (expression.quasis[1] as StringToken).value == '') {
      return (expression.expressions[0] as ExpressionToken).run(context);
    }

    final buf = StringBuffer();
    for (int i = 0; i < expression.expressions.length; ++i) {
      buf.write((expression.quasis[i] as StringToken).value);
      buf.write(
        toString((expression.expressions[i] as ExpressionToken).run(context)),
      );
    }
    buf.write((expression.quasis.last as StringToken).value);

    return buf.toString();
  }

  /// Responsible for processing the ternary operator: test ? consequent : alternate
  @pragma('vm:prefer-inline')
  dynamic condition(
    ConditionToken expression, [
    Map<String, dynamic> context = const {},
  ]);

  /// Responsible for processing the guard operator: test (throws Exception) !: alternate
  @pragma('vm:prefer-inline')
  dynamic guard(
    GuardToken expression, [
    Map<String, dynamic> context = const {},
  ]);

  /// Responsible for processing the unary prefix operator: +-(Integer/Number) | !(Boolean)
  @pragma('vm:prefer-inline')
  dynamic unaryPrefix(
    UnaryPrefixToken expression, [
    Map<String, dynamic> context = const {},
  ]);

  /// Responsible for processing the standard binary operators.
  @pragma('vm:prefer-inline')
  dynamic binary(
    BinaryToken expression, [
    Map<String, dynamic> context = const {},
  ]);

  /// Responsible for processing the standard logical operators.
  @pragma('vm:prefer-inline')
  dynamic logical(
    LogicalToken expression, [
    Map<String, dynamic> context = const {},
  ]);

  /// Responsible for processing the dot operator as member access, only methods: (Object).(Function)
  @pragma('vm:prefer-inline')
  dynamic member(
    MemberToken expression, [
    Map<String, dynamic> context = const {},
  ]);

  /// Responsible for processing the embedded functions.
  @pragma('vm:prefer-inline')
  dynamic function(
    FunctionToken expression, [
    Map<String, dynamic> context = const {},
  ]);

  /// Responsible for processing the literal as const value.
  @pragma('vm:prefer-inline')
  dynamic literal(
    LiteralToken expression, [
    Map<String, dynamic> context = const {},
  ]);

  /// Responsible for processing the reference as variable value.
  @pragma('vm:prefer-inline')
  dynamic reference(
    ReferenceToken expression, [
    Map<String, dynamic> context = const {},
  ]);
}
