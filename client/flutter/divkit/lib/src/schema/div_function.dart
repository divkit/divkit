// Generated code. Do not modify.

import 'package:divkit/src/schema/div_evaluable_type.dart';
import 'package:divkit/src/schema/div_function_argument.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// User-defined function.
class DivFunction extends Preloadable with EquatableMixin {
  const DivFunction({
    required this.arguments,
    required this.body,
    required this.name,
    required this.returnType,
  });

  /// Function argument.
  final List<DivFunctionArgument> arguments;

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
    List<DivFunctionArgument>? arguments,
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
        arguments: safeParseObj(
          safeListMap(
            json['arguments'],
            (v) => safeParseObj(
              DivFunctionArgument.fromJson(v),
            )!,
          ),
        )!,
        body: safeParseStr(
          json['body']?.toString(),
        )!,
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        returnType: safeParseStrEnum(
          json['return_type'],
          parse: DivEvaluableType.fromJson,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivFunction?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivFunction(
        arguments: (await safeParseObjAsync(
          await safeListMapAsync(
            json['arguments'],
            (v) => safeParseObj(
              DivFunctionArgument.fromJson(v),
            )!,
          ),
        ))!,
        body: (await safeParseStrAsync(
          json['body']?.toString(),
        ))!,
        name: (await safeParseStrAsync(
          json['name']?.toString(),
        ))!,
        returnType: (await safeParseStrEnumAsync(
          json['return_type'],
          parse: DivEvaluableType.fromJson,
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await safeFuturesWait(arguments, (v) => v.preload(context));
      await returnType.preload(context);
    } catch (e) {
      return;
    }
  }
}
