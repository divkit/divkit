// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// An arbitrary object in JSON format.
class DictVariable extends Resolvable with EquatableMixin {
  const DictVariable({
    required this.name,
    required this.value,
  });

  static const type = "dict";

  /// Variable name.
  final String name;

  /// Value.
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

  static DictVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DictVariable(
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        value: safeParseMap(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DictVariable resolve(DivVariableContext context) {
    return this;
  }
}
