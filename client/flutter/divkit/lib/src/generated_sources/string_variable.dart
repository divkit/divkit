// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class StringVariable with EquatableMixin {
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

  static StringVariable? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return StringVariable(
      name: safeParseStr(
        json['name']?.toString(),
      )!,
      value: safeParseStr(
        json['value']?.toString(),
      )!,
    );
  }
}
