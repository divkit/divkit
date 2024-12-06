// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Variable — HEX color as a string.
class ColorVariable with EquatableMixin {
  const ColorVariable({
    required this.name,
    required this.value,
  });

  static const type = "color";

  /// Variable name.
  final String name;

  /// Value.
  final Color value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  ColorVariable copyWith({
    String? name,
    Color? value,
  }) =>
      ColorVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static ColorVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return ColorVariable(
        name: reqProp<String>(
          safeParseStr(
            json['name'],
          ),
          name: 'name',
        ),
        value: reqProp<Color>(
          safeParseColor(
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
}
