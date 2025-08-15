// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Specifies the position measured in `dp` from the container start as the scrolling end position. Only applies in `gallery`.
class OffsetDestination with EquatableMixin {
  const OffsetDestination({
    required this.value,
  });

  static const type = "offset";

  /// Position measured in `dp`.
  // constraint: number >= 0
  final Expression<int> value;

  @override
  List<Object?> get props => [
        value,
      ];

  OffsetDestination copyWith({
    Expression<int>? value,
  }) =>
      OffsetDestination(
        value: value ?? this.value,
      );

  static OffsetDestination? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return OffsetDestination(
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
