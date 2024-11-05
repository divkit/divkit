// Generated code. Do not modify.

import 'package:divkit/src/schema/div_evaluable_type.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Function argument.
class DivFunctionArgument extends Resolvable with EquatableMixin {
  const DivFunctionArgument({
    required this.name,
    required this.type,
  });

  /// Function argument name.
  final String name;

  /// Function argument type.
  final DivEvaluableType type;

  @override
  List<Object?> get props => [
        name,
        type,
      ];

  DivFunctionArgument copyWith({
    String? name,
    DivEvaluableType? type,
  }) =>
      DivFunctionArgument(
        name: name ?? this.name,
        type: type ?? this.type,
      );

  static DivFunctionArgument? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivFunctionArgument(
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        type: safeParseStrEnum(
          json['type'],
          parse: DivEvaluableType.fromJson,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivFunctionArgument resolve(DivVariableContext context) {
    type.resolve(context);
    return this;
  }
}
