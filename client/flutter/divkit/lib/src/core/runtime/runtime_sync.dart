import 'package:divkit/src/core/runtime/core.dart';
import 'package:divkit/src/core/runtime/entities.dart';
import 'package:divkit/src/core/runtime/runtime.dart';

/// Synchronous implementation of [Runtime].
class SyncRuntime extends Runtime {
  /// Functions available for use in expressions.
  final dynamic Function(
    FunctionToken function,
    Map<String, dynamic> context,
  ) functions;

  /// Methods available for use in expressions.
  final dynamic Function(
    MemberToken member,
    Map<String, dynamic> context,
  ) members;

  const SyncRuntime({
    required this.functions,
    required this.members,
  });

  @override
  dynamic condition(
    ConditionToken expression, [
    Map<String, dynamic> context = const {},
  ]) {
    final test = execute(expression.test, context);
    if (test is! bool) {
      throw 'Ternary must be called with a Boolean value as a condition';
    }
    return test
        ? execute(expression.consequent, context)
        : execute(expression.alternate, context);
  }

  @override
  dynamic guard(
    GuardToken expression, [
    Map<String, dynamic> context = const {},
  ]) {
    try {
      return execute(expression.test, context) ??
          execute(expression.alternate, context);
    } catch (_) {
      return execute(expression.alternate, context);
    }
  }

  @override
  dynamic unaryPrefix(
    UnaryPrefixToken expression, [
    Map<String, dynamic> context = const {},
  ]) {
    final right = execute(expression.right, context);
    switch (expression.operator) {
      case '-':
        if (right is int) {
          return trueInt(-BigInt.from(right));
        } else if (right is double) {
          return trueNum(-right);
        }
        throw 'A Number is expected after a unary minus';
      case '+':
        if (right is int) {
          return trueInt(right);
        } else if (right is double) {
          return right;
        }
        throw 'A Number is expected after a unary plus';
      case '!':
        if (right is bool) {
          return !right;
        }
        throw 'A Boolean is expected after a unary not';
    }
    throw "Unknown unary operator '${expression.operator}'";
  }

  @override
  dynamic binary(
    BinaryToken expression, [
    Map<String, dynamic> context = const {},
  ]) {
    final left = execute(expression.left, context);
    final right = execute(expression.right, context);
    switch (expression.operator) {
      case '==':
        return equals(left, right);
      case '!=':
        return !equals(left, right);
      case '>=':
        return compare(left, right, '>=') >= 0;
      case '>':
        return compare(left, right, '>') > 0;
      case '<=':
        return compare(left, right, '<=') <= 0;
      case '<':
        return compare(left, right, '<') < 0;
      case '+':
        return sumOp(left, right);
      case '-':
        return subOp(left, right);
      case '/':
        return divOp(left, right);
      case '*':
        return mulOp(left, right);
      case '%':
        return modOp(left, right);
    }
    throw "Unknown binary operator '${expression.operator}'";
  }

  @override
  bool logical(
    LogicalToken expression, [
    Map<String, dynamic> context = const {},
  ]) {
    dynamic left() => execute(expression.left, context);
    dynamic right() => execute(expression.right, context);
    switch (expression.operator) {
      case '&&':
        return cast<bool>(left()) && cast<bool>(right());
      case '||':
        return cast<bool>(left()) || cast<bool>(right());
    }
    throw "Unknown logical operator '${expression.operator}'";
  }

  @override
  dynamic member(
    MemberToken expression, [
    Map<String, dynamic> context = const {},
  ]) {
    return expectResult(
      res: members(
        expression,
        context,
      ),
      onThrow: "Property '${expression.property}' is missing",
    );
  }

  @override
  dynamic function(
    FunctionToken expression, [
    Map<String, dynamic> context = const {},
  ]) {
    return expectResult(
      res: functions(
        expression,
        context,
      ),
      onThrow: "Function '${expression.identifier}' is missing",
    );
  }

  @override
  dynamic literal(
    LiteralToken expression, [
    Map<String, dynamic> context = const {},
  ]) {
    return expression.value;
  }

  @override
  dynamic reference(
    ReferenceToken expression, [
    Map<String, dynamic> context = const {},
  ]) {
    return expectResult(
      res: context[expression.identifier],
      onThrow: "Reference '$expression' is missing",
    );
  }
}
