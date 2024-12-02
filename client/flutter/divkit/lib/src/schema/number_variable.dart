// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// A floating-point variable.
class NumberVariable extends Resolvable with EquatableMixin {
  const NumberVariable({
    required this.name,
    required this.value,
  });

  static const type = "number";

  /// Variable name.
  final String name;

  /// Value.
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

  static NumberVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return NumberVariable(
        name: reqProp<String>(
          safeParseStr(
            json['name'],
          ),
          name: 'name',
        ),
        value: reqProp<double>(
          safeParseDouble(
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

  @override
  NumberVariable resolve(DivVariableContext context) {
    return this;
  }
}
