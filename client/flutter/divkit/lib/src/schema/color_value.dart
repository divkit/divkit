// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
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
        value: reqVProp<Color>(
          safeParseColorExpr(
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

  @override
  ColorValue resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
