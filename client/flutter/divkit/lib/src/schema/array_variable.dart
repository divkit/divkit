// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// An arbitrary array in JSON format.
class ArrayVariable with EquatableMixin {
  const ArrayVariable({
    required this.name,
    required this.value,
  });

  static const type = "array";

  /// Variable name.
  final String name;

  /// Value.
  final Arr value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  ArrayVariable copyWith({
    String? name,
    Arr? value,
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
        name: reqProp<String>(
          safeParseStr(
            json['name'],
          ),
          name: 'name',
        ),
        value: reqProp<Arr>(
          safeParseList(
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
