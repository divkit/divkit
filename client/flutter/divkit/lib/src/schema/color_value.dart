// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class ColorValue extends Resolvable with EquatableMixin {
  const ColorValue({
    required this.value,
  });

  static const type = "color";
  final Expression<Color> value;

  @override
  List<Object?> get props => [
        value,
      ];

  ColorValue copyWith({
    Expression<Color>? value,
  }) =>
      ColorValue(
        value: value ?? this.value,
      );

  static ColorValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return ColorValue(
        value: safeParseColorExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  ColorValue resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
