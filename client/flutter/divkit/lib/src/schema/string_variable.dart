// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// A string variable.
class StringVariable with EquatableMixin {
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
        name: reqProp<String>(
          safeParseStr(
            json['name'],
          ),
          name: 'name',
        ),
        value: reqProp<String>(
          safeParseStr(
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
