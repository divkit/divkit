// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivRadialGradientRelativeCenter with EquatableMixin {
  const DivRadialGradientRelativeCenter({
    required this.value,
  });

  static const type = "relative";

  final Expression<double> value;

  @override
  List<Object?> get props => [
        value,
      ];

  DivRadialGradientRelativeCenter copyWith({
    Expression<double>? value,
  }) =>
      DivRadialGradientRelativeCenter(
        value: value ?? this.value,
      );

  static DivRadialGradientRelativeCenter? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivRadialGradientRelativeCenter(
        value: safeParseDoubleExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}
