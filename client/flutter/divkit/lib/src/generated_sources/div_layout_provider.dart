// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivLayoutProvider with EquatableMixin {
  const DivLayoutProvider({
    this.heightVariableName,
    this.widthVariableName,
  });

  final Expression<String>? heightVariableName;

  final Expression<String>? widthVariableName;

  @override
  List<Object?> get props => [
        heightVariableName,
        widthVariableName,
      ];

  DivLayoutProvider copyWith({
    Expression<String>? Function()? heightVariableName,
    Expression<String>? Function()? widthVariableName,
  }) =>
      DivLayoutProvider(
        heightVariableName: heightVariableName != null
            ? heightVariableName.call()
            : this.heightVariableName,
        widthVariableName: widthVariableName != null
            ? widthVariableName.call()
            : this.widthVariableName,
      );

  static DivLayoutProvider? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivLayoutProvider(
        heightVariableName: safeParseStrExpr(
          json['height_variable_name']?.toString(),
        ),
        widthVariableName: safeParseStrExpr(
          json['width_variable_name']?.toString(),
        ),
      );
    } catch (e) {
      return null;
    }
  }
}
