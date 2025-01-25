// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class ArrayValue with EquatableMixin {
  const ArrayValue({
    required this.value,
  });

  static const type = "array";
  final Expression<Arr> value;

  @override
  List<Object?> get props => [
        value,
      ];

  ArrayValue copyWith({
    Expression<Arr>? value,
  }) =>
      ArrayValue(
        value: value ?? this.value,
      );

  static ArrayValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return ArrayValue(
        value: reqVProp<Arr>(
          safeParseListExpr(
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
