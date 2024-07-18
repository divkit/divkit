// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class ArrayVariable with EquatableMixin {
  const ArrayVariable({
    required this.name,
    required this.value,
  });

  static const type = "array";

  final String name;

  final List<dynamic> value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  ArrayVariable copyWith({
    String? name,
    List<dynamic>? value,
  }) =>
      ArrayVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static ArrayVariable? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return ArrayVariable(
      name: safeParseStr(
        json['name']?.toString(),
      )!,
      value: safeParseList(
        json['value'],
      )!,
    );
  }
}
