// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivLayoutProvider extends Preloadable with EquatableMixin {
  const DivLayoutProvider({
    this.heightVariableName,
    this.widthVariableName,
  });

  final String? heightVariableName;

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
          json['height_variable_name']?.toString(),
        ),
        widthVariableName: safeParseStr(
          json['width_variable_name']?.toString(),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivLayoutProvider?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivLayoutProvider(
        heightVariableName: await safeParseStrAsync(
          json['height_variable_name']?.toString(),
        ),
        widthVariableName: await safeParseStrAsync(
          json['width_variable_name']?.toString(),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {} catch (e) {
      return;
    }
  }
}
