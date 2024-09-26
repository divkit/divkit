import 'package:divkit/src/core/runtime/runtime.dart';
import 'package:equatable/equatable.dart';

abstract class ExpressionToken with EquatableMixin {
  const ExpressionToken();

  @override
  String toString();

  /// Encodes the entire structure of objects in Map (Json).
  Map<String, dynamic> get encoded;

  dynamic map({
    required Function(CompositionToken v) composition,
    required Function(ConditionToken v) condition,
    required Function(GuardToken v) guard,
    required Function(UnaryPrefixToken v) unaryPrefix,
    required Function(BinaryToken v) binary,
    required Function(LogicalToken v) logical,
    required Function(MemberToken v) member,
    required Function(FunctionToken v) function,
    required Function(LiteralToken v) literal,
    required Function(ReferenceToken v) reference,
  });

  /// Execute the expression using [context].
  dynamic run([
    Map<String, dynamic> context = const {},
  ]) =>
      runtime.execute(this, context);
}

class CompositionToken extends ExpressionToken {
  final bool entry;
  final List quasis;
  final List expressions;

  const CompositionToken({
    required this.entry,
    required this.quasis,
    required this.expressions,
  });

  @override
  String toString() {
    final buf = StringBuffer();
    for (int i = 0; i < expressions.length; ++i) {
      buf.write(quasis[i]);
      buf.write(expressions[i]);
    }
    buf.write(quasis.last);

    return buf.toString();
  }

  @override
  List<Object?> get props => [entry, quasis, expressions];

  @override
  Map<String, dynamic> get encoded => {
        'type': 'base',
        'entry': entry,
        'quasis':
            quasis.map((e) => e is ExpressionToken ? e.encoded : e).toList(),
        'expressions': expressions
            .map((e) => e is ExpressionToken ? e.encoded : e)
            .toList(),
      };

  @override
  dynamic map({
    required Function(CompositionToken v) composition,
    required Function(ConditionToken v) condition,
    required Function(GuardToken v) guard,
    required Function(UnaryPrefixToken v) unaryPrefix,
    required Function(BinaryToken v) binary,
    required Function(LogicalToken v) logical,
    required Function(MemberToken v) member,
    required Function(FunctionToken v) function,
    required Function(LiteralToken v) literal,
    required Function(ReferenceToken v) reference,
  }) =>
      composition(this);
}

class ConditionToken extends ExpressionToken {
  final ExpressionToken test;
  final ExpressionToken consequent;
  final ExpressionToken alternate;

  const ConditionToken({
    required this.test,
    required this.consequent,
    required this.alternate,
  });

  @override
  String toString() => '$test ? $consequent : $alternate';

  @override
  List<Object?> get props => [test, consequent, alternate];

  @override
  dynamic map({
    required Function(CompositionToken v) composition,
    required Function(ConditionToken v) condition,
    required Function(GuardToken v) guard,
    required Function(UnaryPrefixToken v) unaryPrefix,
    required Function(BinaryToken v) binary,
    required Function(LogicalToken v) logical,
    required Function(MemberToken v) member,
    required Function(FunctionToken v) function,
    required Function(LiteralToken v) literal,
    required Function(ReferenceToken v) reference,
  }) =>
      condition(this);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'condition',
        'test': test.encoded,
        'consequent': consequent.encoded,
        'alternate': alternate.encoded,
      };
}

class GuardToken extends ExpressionToken {
  final ExpressionToken test;
  final ExpressionToken alternate;

  const GuardToken({
    required this.test,
    required this.alternate,
  });

  @override
  String toString() => '$test !: $alternate';

  @override
  List<Object?> get props => [test, alternate];

  @override
  dynamic map({
    required Function(CompositionToken v) composition,
    required Function(ConditionToken v) condition,
    required Function(GuardToken v) guard,
    required Function(UnaryPrefixToken v) unaryPrefix,
    required Function(BinaryToken v) binary,
    required Function(LogicalToken v) logical,
    required Function(MemberToken v) member,
    required Function(FunctionToken v) function,
    required Function(LiteralToken v) literal,
    required Function(ReferenceToken v) reference,
  }) =>
      guard(this);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'guard',
        'test': test.encoded,
        'alternate': alternate.encoded,
      };
}

class UnaryPrefixToken extends ExpressionToken {
  final String operator;
  final ExpressionToken right;

  const UnaryPrefixToken({
    required this.operator,
    required this.right,
  });

  @override
  String toString() => '$operator$right';

  @override
  List<Object?> get props => [operator, right];

  @override
  dynamic map({
    required Function(CompositionToken v) composition,
    required Function(ConditionToken v) condition,
    required Function(GuardToken v) guard,
    required Function(UnaryPrefixToken v) unaryPrefix,
    required Function(BinaryToken v) binary,
    required Function(LogicalToken v) logical,
    required Function(MemberToken v) member,
    required Function(FunctionToken v) function,
    required Function(LiteralToken v) literal,
    required Function(ReferenceToken v) reference,
  }) =>
      unaryPrefix(this);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'unaryPrefix',
        'operator': operator,
        'right': right.encoded,
      };
}

class BinaryToken extends ExpressionToken {
  final ExpressionToken left;
  final String operator;
  final ExpressionToken right;

  const BinaryToken({
    required this.left,
    required this.operator,
    required this.right,
  });

  @override
  String toString() => '$left $operator $right';

  @override
  List<Object?> get props => [left, operator, right];

  @override
  dynamic map({
    required Function(CompositionToken v) composition,
    required Function(ConditionToken v) condition,
    required Function(GuardToken v) guard,
    required Function(UnaryPrefixToken v) unaryPrefix,
    required Function(BinaryToken v) binary,
    required Function(LogicalToken v) logical,
    required Function(MemberToken v) member,
    required Function(FunctionToken v) function,
    required Function(LiteralToken v) literal,
    required Function(ReferenceToken v) reference,
  }) =>
      binary(this);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'binary',
        'left': left.encoded,
        'operator': operator,
        'right': right.encoded,
      };
}

class LogicalToken extends ExpressionToken {
  final ExpressionToken left;
  final String operator;
  final ExpressionToken right;

  const LogicalToken({
    required this.left,
    required this.operator,
    required this.right,
  });

  @override
  String toString() => '$left $operator $right';

  @override
  List<Object?> get props => [left, operator, right];

  @override
  dynamic map({
    required Function(CompositionToken v) composition,
    required Function(ConditionToken v) condition,
    required Function(GuardToken v) guard,
    required Function(UnaryPrefixToken v) unaryPrefix,
    required Function(BinaryToken v) binary,
    required Function(LogicalToken v) logical,
    required Function(MemberToken v) member,
    required Function(FunctionToken v) function,
    required Function(LiteralToken v) literal,
    required Function(ReferenceToken v) reference,
  }) =>
      logical(this);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'logical',
        'left': left.encoded,
        'operator': operator,
        'right': right.encoded,
      };
}

class MemberToken extends ExpressionToken {
  final ExpressionToken object;
  final ExpressionToken property;

  const MemberToken({
    required this.object,
    required this.property,
  });

  @override
  String toString() => '$object.$property';

  @override
  List<Object?> get props => [object, property];

  @override
  dynamic map({
    required Function(CompositionToken v) composition,
    required Function(ConditionToken v) condition,
    required Function(GuardToken v) guard,
    required Function(UnaryPrefixToken v) unaryPrefix,
    required Function(BinaryToken v) binary,
    required Function(LogicalToken v) logical,
    required Function(MemberToken v) member,
    required Function(FunctionToken v) function,
    required Function(LiteralToken v) literal,
    required Function(ReferenceToken v) reference,
  }) =>
      member(this);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'member',
        'object': object.encoded,
        'property': property.encoded,
      };
}

class FunctionToken extends ExpressionToken {
  final String identifier;
  final List<ExpressionToken> arguments;

  const FunctionToken({
    required this.identifier,
    required this.arguments,
  });

  @override
  String toString() => '$identifier(${arguments.join(', ')})';

  @override
  List<Object?> get props => [identifier, arguments];

  @override
  dynamic map({
    required Function(CompositionToken v) composition,
    required Function(ConditionToken v) condition,
    required Function(GuardToken v) guard,
    required Function(UnaryPrefixToken v) unaryPrefix,
    required Function(BinaryToken v) binary,
    required Function(LogicalToken v) logical,
    required Function(MemberToken v) member,
    required Function(FunctionToken v) function,
    required Function(LiteralToken v) literal,
    required Function(ReferenceToken v) reference,
  }) =>
      function(this);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'function',
        'identifier': identifier,
        'arguments': arguments.map((e) => e.encoded).toList(),
      };
}

abstract class LiteralToken<T extends Object> extends ExpressionToken {
  final T value;

  const LiteralToken(
    this.value,
  );

  @override
  String toString() => value.toString();

  @override
  List<Object?> get props => [value];

  @override
  dynamic map({
    required Function(CompositionToken v) composition,
    required Function(ConditionToken v) condition,
    required Function(GuardToken v) guard,
    required Function(UnaryPrefixToken v) unaryPrefix,
    required Function(BinaryToken v) binary,
    required Function(LogicalToken v) logical,
    required Function(MemberToken v) member,
    required Function(FunctionToken v) function,
    required Function(LiteralToken v) literal,
    required Function(ReferenceToken v) reference,
  }) =>
      literal(this);
}

class ConstToken extends LiteralToken {
  const ConstToken(super.value);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'const',
        'value': value,
      };
}

class IntegerToken extends LiteralToken<int> {
  const IntegerToken(super.value);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'integer',
        'value': value,
      };
}

class NumberToken extends LiteralToken<double> {
  const NumberToken(super.value);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'number',
        'value': value,
      };
}

class BooleanToken extends LiteralToken<bool> {
  const BooleanToken(super.value);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'boolean',
        'value': value,
      };
}

class StringToken extends LiteralToken<String> {
  const StringToken(super.value);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'string',
        'value': value,
      };

  @override
  String toString() => "'$value'";
}

class ReferenceToken extends ExpressionToken {
  final String identifier;

  const ReferenceToken(this.identifier);

  @override
  String toString() => "#$identifier";

  @override
  List<Object?> get props => [identifier];

  @override
  dynamic map({
    required Function(CompositionToken v) composition,
    required Function(ConditionToken v) condition,
    required Function(GuardToken v) guard,
    required Function(UnaryPrefixToken v) unaryPrefix,
    required Function(BinaryToken v) binary,
    required Function(LogicalToken v) logical,
    required Function(MemberToken v) member,
    required Function(FunctionToken v) function,
    required Function(LiteralToken v) literal,
    required Function(ReferenceToken v) reference,
  }) =>
      reference(this);

  @override
  Map<String, dynamic> get encoded => {
        'type': 'reference',
        'identifier': identifier,
      };
}
