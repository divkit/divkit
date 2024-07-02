// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class ColorVariable with EquatableMixin {
  const ColorVariable({
    required this.name,
    required this.value,
  });

  static const type = "color";

  final String name;

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

  static ColorVariable? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return ColorVariable(
      name: safeParseStr(
        json['name']?.toString(),
      )!,
      value: safeParseColor(
        json['value'],
      )!,
    );
  }
}
