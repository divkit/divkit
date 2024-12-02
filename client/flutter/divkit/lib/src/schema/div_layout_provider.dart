// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class DivLayoutProvider extends Resolvable with EquatableMixin {
  const DivLayoutProvider({
    this.heightVariableName,
    this.widthVariableName,
  });

  /// Name of the variable that stores the element’s height.
  final String? heightVariableName;

  /// Name of the variable that stores the element’s width.
  final String? widthVariableName;

  @override
  List<Object?> get props => [
        heightVariableName,
        widthVariableName,
      ];

  DivLayoutProvider copyWith({
    String? Function()? heightVariableName,
    String? Function()? widthVariableName,
  }) =>
      DivLayoutProvider(
        heightVariableName: heightVariableName != null
            ? heightVariableName.call()
            : this.heightVariableName,
        widthVariableName: widthVariableName != null
            ? widthVariableName.call()
            : this.widthVariableName,
      );

  static DivLayoutProvider? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivLayoutProvider(
        heightVariableName: safeParseStr(
          json['height_variable_name'],
        ),
        widthVariableName: safeParseStr(
          json['width_variable_name'],
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivLayoutProvider resolve(DivVariableContext context) {
    return this;
  }
}
