// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Fixed number of repetitions.
class DivFixedCount with EquatableMixin {
  const DivFixedCount({
    required this.value,
  });

  static const type = "fixed";

  /// Number of repetitions.
  // constraint: number >= 0
  final Expression<int> value;

  @override
  List<Object?> get props => [
        value,
      ];

  DivFixedCount copyWith({
    Expression<int>? value,
  }) =>
      DivFixedCount(
        value: value ?? this.value,
      );

  static DivFixedCount? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivFixedCount(
        value: reqVProp<int>(
          safeParseIntExpr(
            json['value'],
          ),
          name: 'value',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
