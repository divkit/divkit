// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// An arbitrary array in JSON format.
class ArrayVariable extends Resolvable with EquatableMixin {
  const ArrayVariable({
    required this.name,
    required this.value,
  });

  static const type = "array";

  /// Variable name.
  final String name;

  /// Value.
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

  static ArrayVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return ArrayVariable(
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        value: safeParseList(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  ArrayVariable resolve(DivVariableContext context) {
    return this;
  }
}
