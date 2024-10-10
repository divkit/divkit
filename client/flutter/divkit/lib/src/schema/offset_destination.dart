// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Specifies position measured in `dp` from container's start as scroll destination. Applicable only in `gallery`.
class OffsetDestination extends Preloadable with EquatableMixin {
  const OffsetDestination({
    required this.value,
  });

  static const type = "offset";

  /// Position in `dp`.
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
        value: safeParseIntExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<OffsetDestination?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return OffsetDestination(
        value: (await safeParseIntExprAsync(
          json['value'],
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
      await value.preload(context);
    } catch (e) {
      return;
    }
  }
}
