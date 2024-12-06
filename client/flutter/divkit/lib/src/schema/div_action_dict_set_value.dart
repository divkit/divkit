// Generated code. Do not modify.

import 'package:divkit/src/schema/div_typed_value.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Sets the value in the dictionary by the specified key. Deletes the key if the value is not set.
class DivActionDictSetValue with EquatableMixin {
  const DivActionDictSetValue({
    required this.key,
    this.value,
    required this.variableName,
  });

  static const type = "dict_set_value";
  final Expression<String> key;
  final DivTypedValue? value;
  final Expression<String> variableName;

  @override
  List<Object?> get props => [
        key,
        value,
        variableName,
      ];

  DivActionDictSetValue copyWith({
    Expression<String>? key,
    DivTypedValue? Function()? value,
    Expression<String>? variableName,
  }) =>
      DivActionDictSetValue(
        key: key ?? this.key,
        value: value != null ? value.call() : this.value,
        variableName: variableName ?? this.variableName,
      );

  static DivActionDictSetValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionDictSetValue(
        key: reqVProp<String>(
          safeParseStrExpr(
            json['key'],
          ),
          name: 'key',
        ),
        value: safeParseObject(
          json['value'],
          parse: DivTypedValue.fromJson,
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
