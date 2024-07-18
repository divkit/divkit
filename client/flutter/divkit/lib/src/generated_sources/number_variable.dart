// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class NumberVariable with EquatableMixin {
  const NumberVariable({
    required this.name,
    required this.value,
  });

  static const type = "number";

  final String name;

  final double value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  NumberVariable copyWith({
    String? name,
    double? value,
  }) =>
      NumberVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static NumberVariable? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return NumberVariable(
      name: safeParseStr(
        json['name']?.toString(),
      )!,
      value: safeParseDouble(
        json['value'],
      )!,
    );
  }
}
