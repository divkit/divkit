// Generated code. Do not modify.

import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Element size adjusts to a parent element.
class DivDefaultIndicatorItemPlacement with EquatableMixin {
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
        spaceBetweenCenters: reqProp<DivFixedSize>(
          safeParseObject(
            json['space_between_centers'],
            parse: DivFixedSize.fromJson,
            fallback: const DivFixedSize(
              value: ValueExpression(
                15,
              ),
            ),
          ),
          name: 'space_between_centers',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
