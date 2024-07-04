// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DictVariable with EquatableMixin {
  const DictVariable({
    required this.name,
    required this.value,
  });

  static const type = "dict";

  final String name;

  final Map<String, dynamic> value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  DictVariable copyWith({
    String? name,
    Map<String, dynamic>? value,
  }) =>
      DictVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static DictVariable? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DictVariable(
      name: safeParseStr(
        json['name']?.toString(),
      )!,
      value: safeParseMap(
        json['value'],
      )!,
    );
  }
}
