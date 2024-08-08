// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class ColorVariable extends Preloadable with EquatableMixin {
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

  static Future<ColorVariable?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return ColorVariable(
        name: (await safeParseStrAsync(
          json['name']?.toString(),
        ))!,
        value: (await safeParseColorAsync(
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
    try {} catch (e) {
      return;
    }
  }
}
