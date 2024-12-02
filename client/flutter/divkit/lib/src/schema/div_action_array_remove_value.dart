// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Deletes a value from the array
class DivActionArrayRemoveValue extends Resolvable with EquatableMixin {
  const DivActionArrayRemoveValue({
    required this.index,
    required this.variableName,
  });

  static const type = "array_remove_value";
  final Expression<int> index;
  final Expression<String> variableName;

  @override
  List<Object?> get props => [
        index,
        variableName,
      ];

  DivActionArrayRemoveValue copyWith({
    Expression<int>? index,
    Expression<String>? variableName,
  }) =>
      DivActionArrayRemoveValue(
        index: index ?? this.index,
        variableName: variableName ?? this.variableName,
      );

  static DivActionArrayRemoveValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionArrayRemoveValue(
        index: reqVProp<int>(
          safeParseIntExpr(
            json['index'],
          ),
          name: 'index',
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
  DivActionArrayRemoveValue resolve(DivVariableContext context) {
    index.resolve(context);
    variableName.resolve(context);
    return this;
  }
}
