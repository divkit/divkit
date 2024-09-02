// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class StringVariable extends Preloadable with EquatableMixin {
  const StringVariable({
    required this.name,
    required this.value,
  });

  static const type = "string";

  final String name;

  final String value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  StringVariable copyWith({
    String? name,
    String? value,
  }) =>
      StringVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static StringVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return StringVariable(
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        value: safeParseStr(
          json['value']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<StringVariable?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return StringVariable(
        name: (await safeParseStrAsync(
          json['name']?.toString(),
        ))!,
        value: (await safeParseStrAsync(
          json['value']?.toString(),
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
