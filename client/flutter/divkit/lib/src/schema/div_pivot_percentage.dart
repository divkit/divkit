// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivPivotPercentage extends Preloadable with EquatableMixin {
  const DivPivotPercentage({
    required this.value,
  });

  static const type = "pivot-percentage";

  final Expression<double> value;

  @override
  List<Object?> get props => [
        value,
      ];

  DivPivotPercentage copyWith({
    Expression<double>? value,
  }) =>
      DivPivotPercentage(
        value: value ?? this.value,
      );

  static DivPivotPercentage? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivPivotPercentage(
        value: safeParseDoubleExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivPivotPercentage?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivPivotPercentage(
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
