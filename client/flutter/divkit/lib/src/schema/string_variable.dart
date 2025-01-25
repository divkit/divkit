// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// A string variable.
class StringVariable extends Resolvable with EquatableMixin {
  const StringVariable({
    required this.name,
    required this.value,
  });

  static const type = "string";

  /// Variable name.
  final String name;

  /// Value.
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

  @override
  StringVariable resolve(DivVariableContext context) {
    return this;
  }
}
