// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// An integer variable.
class IntegerVariable with EquatableMixin {
  const IntegerVariable({
    required this.name,
    required this.value,
  });

  static const type = "integer";

  /// Variable name.
  final String name;

  /// Value.
  final int value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  IntegerVariable copyWith({
    String? name,
    int? value,
  }) =>
      IntegerVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static IntegerVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return IntegerVariable(
        name: reqProp<String>(
          safeParseStr(
            json['name'],
          ),
          name: 'name',
        ),
        value: reqProp<int>(
          safeParseInt(
            json['value'],
          ),
          name: 'value',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
