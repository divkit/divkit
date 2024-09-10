// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Fixed number of repetitions.
class DivFixedCount extends Preloadable with EquatableMixin {
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
        value: safeParseIntExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivFixedCount?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivFixedCount(
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
