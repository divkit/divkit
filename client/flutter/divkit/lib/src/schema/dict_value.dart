// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class DictValue extends Resolvable with EquatableMixin {
  const DictValue({
    required this.value,
  });

  static const type = "dict";
  final Obj value;

  @override
  List<Object?> get props => [
        value,
      ];

  DictValue copyWith({
    Obj? value,
  }) =>
      DictValue(
        value: value ?? this.value,
      );

  static DictValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DictValue(
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
  DictValue resolve(DivVariableContext context) {
    return this;
  }
}
