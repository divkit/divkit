// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Filter based on [calculated expressions](https://divkit.tech/docs/en/concepts/expressions).
class DivInputFilterExpression with EquatableMixin {
  const DivInputFilterExpression({
    required this.condition,
  });

  static const type = "expression";

  /// [Calculated expression](https://divkit.tech/docs/en/concepts/expressions) used to verify the validity of the value.
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
        condition: reqVProp<bool>(
          safeParseBoolExpr(
            json['condition'],
          ),
          name: 'condition',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
