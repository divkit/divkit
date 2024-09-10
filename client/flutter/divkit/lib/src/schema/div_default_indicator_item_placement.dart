// Generated code. Do not modify.

import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Element size adjusts to a parent element.
class DivDefaultIndicatorItemPlacement extends Preloadable with EquatableMixin {
  const DivDefaultIndicatorItemPlacement({
    this.spaceBetweenCenters = const DivFixedSize(
      value: ValueExpression(
        15,
      ),
    ),
  });

  static const type = "default";

  /// Spacing between indicator centers.
  // default value: const DivFixedSize(value: ValueExpression(15,),)
  final DivFixedSize spaceBetweenCenters;

  @override
  List<Object?> get props => [
        spaceBetweenCenters,
      ];

  DivDefaultIndicatorItemPlacement copyWith({
    DivFixedSize? spaceBetweenCenters,
  }) =>
      DivDefaultIndicatorItemPlacement(
        spaceBetweenCenters: spaceBetweenCenters ?? this.spaceBetweenCenters,
      );

  static DivDefaultIndicatorItemPlacement? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivDefaultIndicatorItemPlacement(
        spaceBetweenCenters: safeParseObj(
          DivFixedSize.fromJson(json['space_between_centers']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              15,
            ),
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivDefaultIndicatorItemPlacement?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivDefaultIndicatorItemPlacement(
        spaceBetweenCenters: (await safeParseObjAsync(
          DivFixedSize.fromJson(json['space_between_centers']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              15,
            ),
          ),
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
      await spaceBetweenCenters.preload(context);
    } catch (e) {
      return;
    }
  }
}
