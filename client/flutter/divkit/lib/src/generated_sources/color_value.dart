// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class ColorValue with EquatableMixin {
  const ColorValue({
    required this.value,
  });

  static const type = "color";

  final Expression<Color> value;

  @override
  List<Object?> get props => [
        value,
      ];

  static ColorValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return ColorValue(
      value: safeParseColorExpr(
        json['value'],
      )!,
    );
  }
}
