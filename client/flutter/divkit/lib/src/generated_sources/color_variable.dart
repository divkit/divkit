// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

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
