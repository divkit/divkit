// Generated code. Do not modify.

import 'package:divkit/src/schema/div_typed_value.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Adds a value to the array
class DivActionArrayInsertValue with EquatableMixin {
  const DivActionArrayInsertValue({
    this.index,
    required this.value,
    required this.variableName,
  });

  static const type = "array_insert_value";
  final Expression<int>? index;
  final DivTypedValue value;
  final Expression<String> variableName;

  @override
  List<Object?> get props => [
        index,
        value,
        variableName,
      ];

  DivActionArrayInsertValue copyWith({
    Expression<int>? Function()? index,
    DivTypedValue? value,
    Expression<String>? variableName,
  }) =>
      DivActionArrayInsertValue(
        index: index != null ? index.call() : this.index,
        value: value ?? this.value,
        variableName: variableName ?? this.variableName,
      );

  static DivActionArrayInsertValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionArrayInsertValue(
        index: safeParseIntExpr(
          json['index'],
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
