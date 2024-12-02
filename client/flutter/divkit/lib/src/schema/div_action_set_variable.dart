// Generated code. Do not modify.

import 'package:divkit/src/schema/div_typed_value.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Assigns a value to the variable
class DivActionSetVariable extends Resolvable with EquatableMixin {
  const DivActionSetVariable({
    required this.value,
    required this.variableName,
  });

  static const type = "set_variable";
  final DivTypedValue value;
  final Expression<String> variableName;

  @override
  List<Object?> get props => [
        value,
        variableName,
      ];

  DivActionSetVariable copyWith({
    DivTypedValue? value,
    Expression<String>? variableName,
  }) =>
      DivActionSetVariable(
        value: value ?? this.value,
        variableName: variableName ?? this.variableName,
      );

  static DivActionSetVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSetVariable(
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

  @override
  DivActionSetVariable resolve(DivVariableContext context) {
    value.resolve(context);
    variableName.resolve(context);
    return this;
  }
}
