// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Variable — HEX color as a string.
class ColorVariable extends Resolvable with EquatableMixin {
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
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        value: safeParseColor(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  ColorVariable resolve(DivVariableContext context) {
    return this;
  }
}
