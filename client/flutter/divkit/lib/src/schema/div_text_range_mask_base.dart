// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class DivTextRangeMaskBase with EquatableMixin {
  const DivTextRangeMaskBase({
    this.isEnabled = const ValueExpression(true),
  });

  /// Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
  // default value: true
  final Expression<bool> isEnabled;

  @override
  List<Object?> get props => [
        isEnabled,
      ];

  DivTextRangeMaskBase copyWith({
    Expression<bool>? isEnabled,
  }) =>
      DivTextRangeMaskBase(
        isEnabled: isEnabled ?? this.isEnabled,
      );

  static DivTextRangeMaskBase? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTextRangeMaskBase(
        isEnabled: reqVProp<bool>(
          safeParseBoolExpr(
            json['is_enabled'],
            fallback: true,
          ),
          name: 'is_enabled',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
