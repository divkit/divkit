// Generated code. Do not modify.

import 'package:divkit/src/schema/div_evaluable_type.dart';
import 'package:divkit/src/schema/div_function_argument.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// User-defined function.
class DivFunction with EquatableMixin {
  const DivFunction({
    required this.arguments,
    required this.body,
    required this.name,
    required this.returnType,
  });

  /// Function argument.
  final Arr<DivFunctionArgument> arguments;

  /// Function body. Evaluated as an expression using the passed arguments. Doesn't capture external variables.
  final String body;

  /// Function name.
  // regex: ^[a-zA-Z_][a-zA-Z0-9_]*$
  final String name;

  /// Return value type.
  final DivEvaluableType returnType;

  @override
  List<Object?> get props => [
        arguments,
        body,
        name,
        returnType,
      ];

  DivFunction copyWith({
    Arr<DivFunctionArgument>? arguments,
    String? body,
    String? name,
    DivEvaluableType? returnType,
  }) =>
      DivFunction(
        arguments: arguments ?? this.arguments,
        body: body ?? this.body,
        name: name ?? this.name,
        returnType: returnType ?? this.returnType,
      );

  static DivFunction? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivFunction(
        arguments: reqProp<Arr<DivFunctionArgument>>(
          safeParseObjects(
            json['arguments'],
            (v) => reqProp<DivFunctionArgument>(
              safeParseObject(
                v,
                parse: DivFunctionArgument.fromJson,
              ),
            ),
          ),
          name: 'arguments',
        ),
        body: reqProp<String>(
          safeParseStr(
            json['body'],
          ),
          name: 'body',
        ),
        name: reqProp<String>(
          safeParseStr(
            json['name'],
          ),
          name: 'name',
        ),
        returnType: reqProp<DivEvaluableType>(
          safeParseStrEnum(
            json['return_type'],
            parse: DivEvaluableType.fromJson,
          ),
          name: 'return_type',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
