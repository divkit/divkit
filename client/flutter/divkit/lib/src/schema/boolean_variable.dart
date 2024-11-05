// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// A Boolean variable in binary format.
class BooleanVariable extends Resolvable with EquatableMixin {
  const BooleanVariable({
    required this.name,
    required this.value,
  });

  static const type = "boolean";

  /// Variable name.
  final String name;

  /// Value.
  final bool value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  BooleanVariable copyWith({
    String? name,
    bool? value,
  }) =>
      BooleanVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static BooleanVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return BooleanVariable(
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        value: safeParseBool(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  BooleanVariable resolve(DivVariableContext context) {
    return this;
  }
}
