// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class ColorValue extends Preloadable with EquatableMixin {
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

  static Future<ColorValue?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return ColorValue(
        value: (await safeParseColorExprAsync(
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
