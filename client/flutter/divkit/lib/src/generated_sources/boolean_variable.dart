// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class BooleanVariable with EquatableMixin {
  const BooleanVariable({
    required this.name,
    required this.value,
  });

  static const type = "boolean";

  final String name;

  final bool value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  static BooleanVariable? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return BooleanVariable(
      name: safeParseStr(
        json['name']?.toString(),
      )!,
      value: safeParseBool(
        json['value'],
      )!,
    );
  }
}
