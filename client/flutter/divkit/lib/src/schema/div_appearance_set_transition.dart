// Generated code. Do not modify.

import 'package:divkit/src/schema/div_appearance_transition.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// A set of animations to be applied simultaneously.
class DivAppearanceSetTransition with EquatableMixin {
  const DivAppearanceSetTransition({
    required this.items,
  });

  static const type = "set";

  /// An array of animations.
  // at least 1 elements
  final Arr<DivAppearanceTransition> items;

  @override
  List<Object?> get props => [
        items,
      ];

  DivAppearanceSetTransition copyWith({
    Arr<DivAppearanceTransition>? items,
  }) =>
      DivAppearanceSetTransition(
        items: items ?? this.items,
      );

  static DivAppearanceSetTransition? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivAppearanceSetTransition(
        items: reqProp<Arr<DivAppearanceTransition>>(
          safeParseObjects(
            json['items'],
            (v) => reqProp<DivAppearanceTransition>(
              safeParseObject(
                v,
                parse: DivAppearanceTransition.fromJson,
              ),
            ),
          ),
          name: 'items',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
