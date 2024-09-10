// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class NumberValue extends Preloadable with EquatableMixin {
  const NumberValue({
    required this.value,
  });

  static const type = "number";
  final Expression<double> value;

  @override
  List<Object?> get props => [
        value,
      ];

  NumberValue copyWith({
    Expression<double>? value,
  }) =>
      NumberValue(
        value: value ?? this.value,
      );

  static NumberValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return NumberValue(
        value: safeParseDoubleExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<NumberValue?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return NumberValue(
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
