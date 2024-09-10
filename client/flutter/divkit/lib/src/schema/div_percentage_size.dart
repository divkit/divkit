// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Element size (%).
class DivPercentageSize extends Preloadable with EquatableMixin {
  const DivPercentageSize({
    required this.value,
  });

  static const type = "percentage";

  /// Element size value.
  // constraint: number > 0
  final Expression<double> value;

  @override
  List<Object?> get props => [
        value,
      ];

  DivPercentageSize copyWith({
    Expression<double>? value,
  }) =>
      DivPercentageSize(
        value: value ?? this.value,
      );

  static DivPercentageSize? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivPercentageSize(
        value: safeParseDoubleExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivPercentageSize?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivPercentageSize(
        value: (await safeParseDoubleExprAsync(
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
