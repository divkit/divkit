// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// [Calculated expression](https://divkit.tech/docs/en/concepts/expressions) filter.
class DivInputFilterExpression extends Preloadable with EquatableMixin {
  const DivInputFilterExpression({
    required this.condition,
  });

  static const type = "expression";

  /// [Calculated expression](https://divkit.tech/docs/en/concepts/expressions) used as a value validity condition.
  final Expression<bool> condition;

  @override
  List<Object?> get props => [
        condition,
      ];

  DivInputFilterExpression copyWith({
    Expression<bool>? condition,
  }) =>
      DivInputFilterExpression(
        condition: condition ?? this.condition,
      );

  static DivInputFilterExpression? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivInputFilterExpression(
        condition: safeParseBoolExpr(
          json['condition'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivInputFilterExpression?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivInputFilterExpression(
        condition: (await safeParseBoolExprAsync(
          json['condition'],
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await condition.preload(context);
    } catch (e) {
      return;
    }
  }
}
