// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
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
  final Obj value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  DictVariable copyWith({
    String? name,
    Obj? value,
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
        name: reqProp<String>(
          safeParseStr(
            json['name'],
          ),
          name: 'name',
        ),
        value: reqProp<Obj>(
          safeParseMap(
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
  DictVariable resolve(DivVariableContext context) {
    return this;
  }
}
