// Generated code. Do not modify.

import 'package:divkit/src/schema/div_evaluable_type.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Function argument.
class DivFunctionArgument with EquatableMixin {
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
        name: reqProp<String>(
          safeParseStr(
            json['name'],
          ),
          name: 'name',
        ),
        type: reqProp<DivEvaluableType>(
          safeParseStrEnum(
            json['type'],
            parse: DivEvaluableType.fromJson,
          ),
          name: 'type',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
