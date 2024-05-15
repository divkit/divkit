// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

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

  static DivRadialGradientRelativeCenter? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivRadialGradientRelativeCenter(
      value: safeParseDoubleExpr(
        json['value'],
      )!,
    );
  }
}
