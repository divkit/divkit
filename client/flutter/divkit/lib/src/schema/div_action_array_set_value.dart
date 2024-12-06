// Generated code. Do not modify.

import 'package:divkit/src/schema/div_typed_value.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Sets the value in the array by index.
class DivActionArraySetValue with EquatableMixin {
  const DivActionArraySetValue({
    required this.index,
    required this.value,
    required this.variableName,
  });

  static const type = "array_set_value";
  final Expression<int> index;
  final DivTypedValue value;
  final Expression<String> variableName;

  @override
  List<Object?> get props => [
        index,
        value,
        variableName,
      ];

  DivActionArraySetValue copyWith({
    Expression<int>? index,
    DivTypedValue? value,
    Expression<String>? variableName,
  }) =>
      DivActionArraySetValue(
        index: index ?? this.index,
        value: value ?? this.value,
        variableName: variableName ?? this.variableName,
      );

  static DivActionArraySetValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionArraySetValue(
        index: reqVProp<int>(
          safeParseIntExpr(
            json['index'],
          ),
          name: 'index',
        ),
        value: reqProp<DivTypedValue>(
          safeParseObject(
            json['value'],
            parse: DivTypedValue.fromJson,
          ),
          name: 'value',
        ),
        variableName: reqVProp<String>(
          safeParseStrExpr(
            json['variable_name'],
          ),
          name: 'variable_name',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
